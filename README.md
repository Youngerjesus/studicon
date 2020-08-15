<img src="http://www.thestudicon.com/images/logo_sm.png" alt="Logo" width="100%" height="100"/>

## ➤ Inspiration
_____ 
#### It is a Blockchain-based platform of managing study group. It  provides study-managing tools and certificate. This can provide study operators with ease of management and study members with motivation.
+ Vision : To rev up study culture

_____
+ Background<br>
Study was closed, mostly with acquaintances.
Lack of transparency and systematicity in managing rules
Difficult to prove participation of study group 
 
+ Why Blockchain? <br> 
    - Blockchain opens all records transparently. It increases trust of study operation
    - Information on the block chain is impossible to forge and remain permanently. The history of study group is recorded and this platform issue certificate based on it
    

+ Service Feature
    - Study Recruitment and Participation <br>
    - Automate Managing Study Rule and Giving Penalty <br>
    - Incentive System for motivation <br>
    - Issue certificate of completion <br>


+ Expected effect
    - study member.. <br>
    motivate themselves by penalty and incentive system.
    - study chief.. <br>
    automate managing rules of penalty and incentive system.
    - study culture.. <br>
    Extend study group to a wider range from current  acquaintance-oriented study.


[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)](#what-it-does)

## ➤ Usage 
--- 
:	import project
```
    git clone https://github.com/Youngerjesus/studicon.git
```
:	edit application-dev.properties & application.properties
```
   app.host=http://localhost:8080
   server.port=8080
```
:	create jar file  
```
   mvn package 
```
### 


[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)](#how-i-built-it)

## ➤ How I Built It
---
### Source Code Management

- We created a remote server configuration from Intellij IDEA Ultimate, and synced our works in real time.
- Specific methods are described in the following link : [Create a remote server configuration](https://www.jetbrains.com/help/idea/creating-a-remote-server-configuration.html)
---
### Languages and Frameworks
  
| Languages and Frameworks      | version |
| ----------------------------- | ------: |
| Java                          | 11.0.8  |
| spring-boot-starter-data-jpa  | 2.2.4   |
| spring-boot-starter-mail      | 2.2.4   |
| spring-boot-starter-security  | 2.2.4   |
| spring-boot-starter-thymeleaf | 2.2.4   |
| spring-boot-starter-web       | 2.2.4   |
| modelmapper                   | 2.3.6   |
| querydsl-jpa                  | 4.2.2   |
| postgresql                    | 42.2.9  |
| icon-sdk                      | 0.9.15  |
---
### Cloud Services
- [Amazon Lightsail](https://aws.amazon.com/ko/free/compute/lightsail/?trk=ps_a134p000003yHYqAAM&trkCampaign=acq_paid_search_brand&sc_channel=PS&sc_campaign=acquisition_KR&sc_publisher=Google&sc_category=Cloud%20Computing&sc_country=KR&sc_geo=APAC&sc_outcome=acq&sc_detail=amazon%20lightsail&sc_content=Lightsail_e&sc_segment=444218216084&sc_medium=ACQ-P|PS-GO|Brand|Desktop|SU|Cloud%20Computing|Product|KR|EN|Text&s_kwcid=AL!4422!3!444218216084!e!!g!!amazon%20lightsail&ef_id=CjwKCAjwydP5BRBREiwA-qrCGmYydTrGz-NhiZ-mfrP4JGlo2PUnPkTCm44g2asOlIiKUl4k8zMUFBoCS9MQAvD_BwE:G:s&s_kwcid=AL!4422!3!444218216084!e!!g!!amazon%20lightsail)
- [Amazon Relational Database Service](https://aws.amazon.com/ko/rds/)
- [Amazon Route 53](https://aws.amazon.com/ko/route53/)
---
### Score Variables and Methods
`Studicon.py` is an ICON Smart Contract that manages the major functions of Studicon service, deposit and certificates
#### Variables
```python
self._allMemberList = ArrayDB("allMemberList", db, value_type = int)
```
: For managing accounts who join the study
```python
self._memberStatus = DictDB("memberStatus", db, value_type = str)
```
:	For updating each study member status, JOIN, LEAVE, EXPEL, or GRADUATE
```python
self._studyStatus = VarDB("studyStatus", db, value_type = str)
```
:	For updating study status, OPEN or CLOSE
```python
self._depositInfo = DictDB("depositInfo", db, value_type = int)
```
:	For managing study deposit of each member
#### Read only methods
```python
@external(readonly=True)
def getMemberDepositInfo(self, _accountId: int) -> int:
```
:	Get the amount of remain deposit owned each member
```python
@external(readonly=True)
def getCommonDepositInfo(self) -> int:
```
:	Get the amount of deposit owned SCORE address
```python
@external(readonly=True)
def getPayBackDepositInfo(self, _accountId: int) -> int:
```
:	Get the amount of pay back deposit for each member

#### External methods
```python
@external
@payable
def openStudy(self, _attendPenalty: int, _homeworkPenalty: int, _initDeposit: int):
```
:	Publish study and set initial deposit and penalties
```python
@external
@payable
def joinStudy(self, _accountId: int):
```
:	Register the account who joins the study with paying initial deposit

```python
@external
def leaveStudy(self, _accountId: int) -> bool:
```
:	Remove the account who leaves the study with paying back the remain deposit
```python
@external
def applyPenalty(self, _accountId: int):
```
:	Apply the penalty, absence, or incomplete assignment, to the member determined by voting
: Reduced deposit is stored at the SCORE address
```python
def expelMember(self, _accountId: int):
```
:	Expel the member who no longer has the deposit left
```python
@external
def closeStudy(self):
```
:	Close study and graduate the members who has the deposit left
```python
@external
def payBackDeposit(self, _accountId: int) -> bool:
```
:	Pay back the member-owned and SCORE-owned deposit


[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)](#challenges-i-ran-into)

## ➤ Challenges I ran into
------------------------------------------------
Every moments during our projects was a challenge for us. Our team has been studying blockchain since this year and this project was the first official project.
During our project, there were some several challenges, and we have learned many things from this them.
	
### Designing Service - Why Blockchain?
- When designing the Dapp,  we need to think about the reasons why blockchain should be applied.  
- We spent many times discussing about idea.  
- We needed the idea which is attractive as itself, can get benefit from the blockchain, and is realistic.
---
### Writing our new SCORE and deploying our SCORE 
- It was the first time for us to write score and deploy it with t-bears environments in docker.
- We studied for few weeks about writing and deploying SCORE.
---
### Adapting SCORE in our application
- We need to make java-sdk to adapt the score in our application.
---
### Constructing Server with Spring Boot
- We decided to learn and use Spring Boot framework as our web framework.

[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)](#accomplishment-that-im-proud-of)

## ➤ Accomplishment that I’m proud of.
------------------------------------
	
### Our own SCORE
- We wrote our own SCORE- Studicon
- Studicon is our own SCORE which controls the deposit of users in our service.
- We are proud the fact that we can contribute to the ecosystem of icon. 
		
### Our own Dapp
- We designed our service in the aspect of business to get the real profit.
- We are ready launch our service, even we prepared AWS domain. 
- We are going to keep monitoring our detail code and test server so that we can improve some operations and fix bugs to launch Studicon.

###  Contribution to the popularization of blockchain and icon  network.
- Over conceptual study about blockchain, we have learned the way to “discuss” how blockchain can be applied to our daily life.
- Though there have been several trials, but  blockchain technology is still quite unfamiliar to ordinary people. 
- We designed our idea for ordinary people, especially 20-30 ages who are looking for the study-platform.
- The slogan of our team is to lower the entry barrier of blockchain technology.
- We hope our service can make blockchain technology more familiar to public.    


[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)](#what-i-learned)

## ➤ What I learned
-----------------

###  The  ICON Network and Writing SCORE.
- Not only learning how to write SCORE, we studied  icon network and icon loop chain for several weeks with regular session seminar.
- We read the white paper of icon and discussed the components, principal, governance of icon ecosystem.
- Especially, we are interested in deploying NFT and we found many practical application of NFT deployed by icon.       
---	 
### Improved developing skills.
- Through this project, we got more familiar with web developing skills.
- We studied several Web programming skills such as Spring, Spring JPA and Security, Thymeleaf and so on.
- Also, we  get more familiar with block chain development environment.
- We learned how to set the docker virtual environment with t-bears and how to deploy it with key-store file.

---
### The developing process of Dapp
- Through the entire process of project, we learned entire way of dapp developing process.
- The most important thing among what we have learned is that, we should keep thinking “why blockchain” 
- Also, we learned how SCORE can be adapted to our web application through java sdk and what we should consider during programming web application.

[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)](#whats-next-for-skkrypto)

## ➤ What’s next for Skkrypto
---------------------------
	
As it is said before, the object of our team-Skkrypto  is to lower the entry barrier of blockchain technology.

We will keep monitoring Studicon and also do various tries to take parts in icon network.   

### Introducing ’Study Token’ concepts to the Studicon

- Still, it is ICX which is exchanged btween users of Studicon.
- we have plans to introduce ’Study Coin’ concepts to our service, which can enable each study to deploy their own study token.
- The following is the detail user flow of “Study Token”. 
  1.  When Study is established, the leader of study can deploy the unique token for the study.
  2. Users who want to join the study should buy the token of study.
  3. There might be the auction system to buy the token of study, which means if study get famous, the token of the study will be more expensive. 
---
### Study Token can be exchanged with ICX.
- The following is the expected benefits of introducing study token into Studicon.
  1. Study Token can prevent malicious participants joining study.
  2. Through Study Token, there can be a study with high-quality and give incentives to keep study over and over.
  3. It makes easier to introduce other gaming events to Studicon and makes service much more unique.
---	
### What’s next



[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)](#contributors)

## ➤ Contributors
	

| [<img alt="JeongMin Yeo" src="https://avatars2.githubusercontent.com/u/28587291?s=460&u=6f0ea3151a3dbc07155e95fc5a3e9e156cbd5cb5&v=4" width="100">](hhttps://github.com/Youngerjesus) | [<img alt="SeungHo Park" src="https://avatars0.githubusercontent.com/u/63057703?s=460&v=4" width="100">](https://github.com/JoonPark0221) | [<img alt="JunHo Bae" src="https://avatars3.githubusercontent.com/u/48244064?s=460&u=12eeafb82be3cc079c6247c6c2eb0ae1d81a4cb7&v=4" width="100">](https://github.com/JunhoBae999) | [<img alt="YongWook Lee" src="https://avatars2.githubusercontent.com/u/46441280?s=460&v=4" width="100">](https://github.com/yongwookLee) | [<img alt="JiYeon Jin" src="https://avatars2.githubusercontent.com/u/46441280?s=460&v=4" width="100">](https://instagram.com/skkrypto) | [<img alt="DongWoo Jeong" src="https://avatars3.githubusercontent.com/u/63057683?s=460&v=4" width="100">](https://github.com/DongwooJeong) |
|:--------------------------------------------------:|:--------------------------------------------------:|:--------------------------------------------------:|:--------------------------------------------------:|:--------------------------------------------------:|:--------------------------------------------------:|
| [JeongMin Yeo](hhttps://github.com/Youngerjesus) | [SeungHo Park](https://github.com/JoonPark0221)  | [JunHo Bae](https://github.com/JunhoBae999)      | [YongWook Lee](https://github.com/yongwookLee)   | [JiYeon Jin](https://instagram.com/skkrypto)     | [DongWoo Jeong](https://github.com/DongwooJeong) |
| 🔥🔥🔥                                           | 🔥🔥🔥                                           | 🔥🔥🔥                                           | 🔥🔥🔥                                           | 🔥🔥🔥                                           | 🔥🔥🔥                                           |


| [<img alt="MinSeung Shin" src="https://avatars2.githubusercontent.com/u/53426778?s=460&v=4" width="100">](https://github.com/minseungShin) |
|:--------------------------------------------------:|
| [MinSeung Shin](https://github.com/minseungShin) |
| 🔥🔥🔥                                           |


[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)](#license)

## ➤ License
	
Licensed under [MIT](https://opensource.org/licenses/MIT).