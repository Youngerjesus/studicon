from iconservice import *

TAG = 'studicon'

# An interface of tokenFallback.
# Receiving SCORE that has implemented this interface can handle
# the receiving or further routine.
class TokenFallbackInterface(InterfaceScore):
    @interface
    def tokenFallback(self, _from: Address, _value: int, _data: bytes):
        pass


class studicon(IconScoreBase):

    def __init__(self, db: IconScoreDatabase) -> None:
        super().__init__(db)
        self._allMemberList = ArrayDB("allMemberList", db, value_type = int)
        self._numberOfJoinMember = VarDB("numberOfJoinMember", db, value_type = int)
        self._studyManager = VarDB("studyManager", db, value_type = int)
        self._studyStatus = VarDB("studyStatus", db, value_type = str)
        self._studyTxHash = DictDB("studyTxHash", db, value_type = bytes)
        self._memberStatus = DictDB("memberStatus", db, value_type = str)
        self._memberTxHash = DictDB("memberTxHash", db, value_type = bytes, depth = 2)
        self._depositInfo = DictDB("depositInfo", db, value_type = int)
        self._penaltyList = DictDB("penaltyList", db, value_type = int)

    def on_install(self, _managerAccountId: int) -> None:
        super().on_install()
        self._studyManager.set(_managerAccountId)

    def on_update(self) -> None:
        super().on_update()

    @external(readonly=True)
    def getMemberStatus(self, _accountId: int) -> str:
        return self._memberStatus[_accountId]

    @external(readonly=True)
    def getMemberDepositInfo(self, _accountId: int) -> int:
        return self._depositInfo[_accountId]

    @external(readonly=True)
    def getCommonDepositInfo(self) -> int:
        return self._depositInfo["common"]

    @external(readonly=True)
    def getPayBackDepositInfo(self, _accountId: int) -> int:
        if (self._memberStatus[_accountId] == "GRADUATE") :
            return self._depositInfo[_accountId] + self._depositInfo["allocate"]
        elif (self._memberStatus[_accountId] == "FINISH") :
            return self._depositInfo[_accountId]
        else :
            revert("Not a graduate member")

    @external(readonly=True)
    def getTotalBalance(self) -> int:
        return self.icx.get_balance(self.address)

    @external
    @payable
    def openStudy(self, _attendPenalty: int, _homeworkPenalty: int, _initDeposit: int):
        managerId = self._studyManager.get()
        if (self._memberStatus[managerId] == "EXPEL") :
            revert("Exepelled member")
        elif (self._memberStatus[managerId] == "LEAVE") :
            revert("Left member")
        elif (self.msg.value < self._depositInfo["initDeposit"]) :
            revert("Not enough deposit")
        else :
            self._studyStatus.set("OPEN")
            self._studyTxHash["open"] = self.tx.hash
            self._penaltyList["attend"] = _attendPenalty * (10 ** 18)
            self._penaltyList["homework"] = _homeworkPenalty * (10 ** 18)
            self._depositInfo["initDeposit"] = _initDeposit * (10 ** 18)
            self._memberStatus[managerId] = "JOIN"
            self._memberTxHash[managerId]["join"] = self.tx.hash
            self._allMemberList.put(managerId)
            self._numberOfJoinMember.set(self._numberOfJoinMember.get() + 1)
            self._depositInfo[managerId] = self.msg.value

    @external
    @payable
    def joinStudy(self, _accountId: int):
        if (self._studyStatus.get() != "OPEN") :
            revert("Study is closed")
        elif (self._memberStatus[_accountId] == "EXPEL") :
            revert("Exepelled member")
        elif (self._memberStatus[_accountId] == "LEAVE") :
            revert("Left member")
        elif (self.msg.value < self._depositInfo["initDeposit"]) :
            revert("Not enough deposit")
        else :
            self._memberStatus[_accountId] = "JOIN"
            self._memberTxHash[_accountId]["join"] = self.tx.hash
            self._allMemberList.put(_accountId)
            self._numberOfJoinMember.set(self._numberOfJoinMember.get() + 1)
            self._depositInfo[_accountId] = self.msg.value

    @external
    def leaveStudy(self, _accountId: int) -> bool:
        if (self._studyStatus.get() != "OPEN") :
            revert("Study is closed")
        elif (self._memberStatus[_accountId] != "JOIN") :
            revert("Not a join member")
        else :
            amount = self._depositInfo[_accountId]
            self._memberStatus[_accountId] = "LEAVE"
            self._memberTxHash[_accountId]["leave"] = self.tx.hash
            self._numberOfJoinMember.set(self._numberOfJoinMember.get() - 1)
            self._depositInfo[_accountId] = 0
            return self.icx.send(self.msg.sender, amount)

    @external
    def applyAttendPenalty(self, _accountId: int):
        deposit = self._depositInfo[_accountId]
        offset = self._penaltyList["attend"]
        if (self._studyStatus.get() != "OPEN") :
            revert("Study is closed")
        elif (self._memberStatus[_accountId] != "JOIN") :
            revert("Not a join member")
        elif (deposit - offset <= 0) :
            self.expelMember(_accountId)
        else :
            self._depositInfo[_accountId] = deposit - offset
            self._depositInfo["common"] = self._depositInfo["common"] + offset

    @external
    def applyHomeworkPenalty(self, _accountId: int):
        deposit = self._depositInfo[_accountId]
        offset = self._penaltyList["homework"]
        if (self._studyStatus.get() != "OPEN") :
            revert("Study is closed")
        elif (self._memberStatus[_accountId] != "JOIN") :
            revert("Not a join member")
        elif (deposit - offset <= 0) :
            self.expelMember(_accountId)
        else :
            self._depositInfo[_accountId] = deposit - offset
            self._depositInfo["common"] = self._depositInfo["common"] + offset

    def expelMember(self, _accountId: int):
        self._depositInfo["common"] = self._depositInfo["common"] + self._depositInfo[_accountId]
        self._memberStatus[_accountId] = "EXPEL"
        self._memberTxHash[_accountId]["expel"] = self.tx.hash
        self._depositInfo[_accountId] = 0
        self._numberOfJoinMember.set(self._numberOfJoinMember.get() - 1)

    @external
    def closeStudy(self):
        if (self._studyStatus.get() != "OPEN") :
            revert("Already closed")
        else :
            self._studyStatus.set("CLOSE")
            self._studyTxHash["close"] = self.tx.hash
            self._depositInfo["allocate"] = int(self._depositInfo["common"] / self._numberOfJoinMember.get())
            for e in self._allMemberList :
                if (self._memberStatus[e] == "JOIN") :
                    self._memberStatus[e] = "GRADUATE"
                    self._memberTxHash[e]["graduate"] = self.tx.hash

    @external
    def payBackDeposit(self, _accountId: int) -> bool:
        if (self._studyStatus.get() != "CLOSE") :
            revert("Study is not closed")
        elif (self._memberStatus[_accountId] != "GRADUATE") :
            revert("Not a graduate member")
        else :
            amount = self._depositInfo[_accountId] + self._depositInfo["allocate"]
            self._depositInfo[_accountId] = 0
            self._memberStatus[_accountId] = "FINISH"
            self._depositInfo["common"] = self._depositInfo["common"] - self._depositInfo["allocate"]
            return self.icx.send(self.msg.sender, amount)
