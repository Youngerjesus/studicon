package com.studyolle.modules.study;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.study.event.StudyCreatedEvent;
import com.studyolle.modules.study.event.StudyUpdateEvent;
import com.studyolle.modules.tag.Tag;
import com.studyolle.modules.zone.Zone;
import com.studyolle.modules.study.form.StudyDescriptionForm;
import foundation.icon.icx.*;
import foundation.icon.icx.data.Address;
import foundation.icon.icx.data.Bytes;
import foundation.icon.icx.data.TransactionResult;
import foundation.icon.icx.transport.http.HttpProvider;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static com.studyolle.modules.study.form.StudyForm.VALID_PATH_PATTERN;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository repository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    public Study createNewStudy(Study study, Account account) {
        Study newStudy = repository.save(study);
        newStudy.addManager(account);
        return newStudy;
    }

    public Study getStudyToUpdate(Account account, String path) {
        Study study = this.getStudy(path);
        checkIfManager(account, study);
        return study;
    }

    public Study getStudy(String path) {
        Study study = this.repository.findByPath(path);
        checkIfExistingStudy(path, study);
        return study;
    }

    public void updateStudyDescription(Study study, StudyDescriptionForm studyDescriptionForm) {
        modelMapper.map(studyDescriptionForm, study);
        eventPublisher.publishEvent(new StudyUpdateEvent(study, "스터디 소개를 수정했습니다."));
    }

    /* seungho */
    @Transactional
    public void updateStudyScoreAddress(String path, Long managerId) {
        Study study = repository.findStudyOnlyByPath(path);

        byte[] content = new byte[1024];
        try {
            content = Files.readAllBytes(Paths.get("./content.zip"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpProvider httpProvider = new HttpProvider("https://bicon.net.solidwallet.io/api/v3");
        IconService iconService = new IconService(httpProvider);
        BigInteger networkId = new BigInteger("3");
        Wallet wallet = KeyWallet.load(new Bytes("0xb7a7f8c25301cd4a630fdb4f9b61c0d879ca7d6b9e67059a65fc2ccb1c0c2d8c"));
        long timestamp = System.currentTimeMillis() * 1000L;

        RpcObject paramsDeploy = new RpcObject.Builder()
                .put("_managerAccountId", new RpcValue(new BigInteger(String.valueOf(managerId))))
                .build();
        Transaction transactionDeploy = TransactionBuilder.newBuilder()
                .nid(networkId)
                .from(wallet.getAddress())
                .to(new Address("cx0000000000000000000000000000000000000000"))
                .timestamp(new BigInteger(Long.toString(timestamp)))
                .deploy("application/zip", content)
                .params(paramsDeploy)
                .build();
        try {
            SignedTransaction signedTransaction = new SignedTransaction(transactionDeploy, wallet, new BigInteger(String.valueOf(0x40000000)));
            Bytes txHash = iconService.sendTransaction(signedTransaction).execute();

            TimeUnit.SECONDS.sleep(5);

            TransactionResult result = iconService.getTransactionResult(txHash).execute();
            study.setScoreAddress(result.getScoreAddress());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    /* seungho */

    public void updateStudyImage(Study study, String image) {
        study.setImage(image);
    }

    public void enableStudyBanner(Study study) {
        study.setUseBanner(true);
    }

    public void disableStudyBanner(Study study) {
        study.setUseBanner(false);
    }

    public void addTag(Study study, Tag tag) {
        study.getTags().add(tag);
    }

    public void removeTag(Study study, Tag tag) {
        study.getTags().remove(tag);
    }

    public void addZone(Study study, Zone zone) {
        study.getZones().add(zone);
    }

    public void removeZone(Study study, Zone zone) {
        study.getZones().remove(zone);
    }

    public Study getStudyToUpdateTag(Account account, String path) {
        Study study = repository.findStudyWithTagsByPath(path);
        checkIfExistingStudy(path, study);
        checkIfManager(account, study);
        return study;
    }

    public Study getStudyToUpdateZone(Account account, String path) {
        Study study = repository.findStudyWithZonesByPath(path);
        checkIfExistingStudy(path, study);
        checkIfManager(account, study);
        return study;
    }

    public Study getStudyToUpdateStatus(Account account, String path) {
        Study study = repository.findStudyWithManagersByPath(path);
        checkIfExistingStudy(path, study);
        checkIfManager(account, study);
        return study;
    }

    public Study getStudyToUpdateStatusWithoutManager(Account account, String path) {
        Study study = repository.findStudyWithManagersByPath(path);
        checkIfExistingStudy(path, study);
        return study;
    }


    private void checkIfManager(Account account, Study study) {
        if (!study.isManagedBy(account)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }


    private void checkIfExistingStudy(String path, Study study) {
        if (study == null) {
            throw new IllegalArgumentException(path + "에 해당하는 스터디가 없습니다.");
        }
    }

    public void publish(Study study) {
        study.publish();
        this.eventPublisher.publishEvent(new StudyCreatedEvent(study));
    }

    public void close(Study study) {
        study.close();
        eventPublisher.publishEvent(new StudyUpdateEvent(study, "스터디를 종료했습니다."));
    }

    public void startRecruit(Study study) {
        study.startRecruit();
        eventPublisher.publishEvent(new StudyUpdateEvent(study, "팀원 모집을 시작합니다."));
    }

    public void stopRecruit(Study study) {
        study.stopRecruit();
        eventPublisher.publishEvent(new StudyUpdateEvent(study, "팀원 모집을 중단했습니다."));
    }

    public boolean isValidPath(String newPath) {
        if (!newPath.matches(VALID_PATH_PATTERN)) {
            return false;
        }

        return !repository.existsByPath(newPath);
    }

    public void updateStudyPath(Study study, String newPath) {
        study.setPath(newPath);
    }

    public boolean isValidTitle(String newTitle) {
        return newTitle.length() <= 50;
    }

    public void updateStudyTitle(Study study, String newTitle) {
        study.setTitle(newTitle);
    }

    /* seungho */
    public boolean isValidInitDeposit(Integer newInitDeposit) {
        return newInitDeposit > 0;
    }

    public void updateStudyInitDeposit(Study study, Integer newInitDeposit) {
        study.setInitDeposit(newInitDeposit);
    }

    public boolean isValidAttendPenalty(Integer newAttendPenalty) {
        return newAttendPenalty > 0;
    }

    public void updateStudyAttendPenalty(Study study, Integer newAttendPenalty) {
        study.setAttendPenalty(newAttendPenalty);
    }

    public boolean isValidIHomeworkPenalty(Integer newHomeworkPenalty) {
        return newHomeworkPenalty > 0;
    }

    public void updateStudyHomeworkPenalty(Study study, Integer newHomeworkPenalty) {
        study.setHomeworkPenalty(newHomeworkPenalty);
    }

    public void updateStudyCloseStudyTxHash(Study study, String txHash) {
        study.setCloseStudyTxHash(txHash);
    }
    /* seungho */

    public void remove(Study study) {
        if (study.isRemovable()) {
            repository.delete(study);
        } else {
            throw new IllegalArgumentException("스터디를 삭제할 수 없습니다.");
        }
    }

    public void addMember(Study study, Account account) {
        study.addMember(account);
    }

    public void removeMember(Study study, Account account) {
        study.removeMember(account);
    }

    public Study getStudyToEnroll(String path) {
        Study study = repository.findStudyOnlyByPath(path);
        checkIfExistingStudy(path, study);
        return study;
    }

}
