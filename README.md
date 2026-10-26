# Online Examination System — Jenkins CI/CD

A Java 17 Servlet application packaged as a WAR, tested with JUnit 5, and deployed to Apache Tomcat 10.1 by Jenkins.

## Requirements
- JDK 17
- Maven 3.9+
- Git
- Jenkins with Pipeline, Git, JUnit, Credentials Binding, and Workspace Cleanup plugins
- Apache Tomcat 10.1
- A Linux/Unix Jenkins agent for this Jenkinsfile (`sh` commands)

## A. Run tests and package locally
From the project root:
```bash
mvn clean test package
```
Expected: `BUILD SUCCESS`, four tests pass, and `target/exam-system.war` exists.

## B. Configure Tomcat Manager
In `$CATALINA_HOME/conf/tomcat-users.xml`, add these inside the existing `<tomcat-users>` element. Use your own strong password:
```xml
<role rolename="manager-script"/>
<user username="deployer" password="REPLACE_WITH_A_STRONG_PASSWORD" roles="manager-script"/>
```
Restart Tomcat. Check the text API:
```bash
curl -u deployer:REPLACE_WITH_A_STRONG_PASSWORD http://localhost:8080/manager/text/list
```
The response should begin with `OK`. Tomcat Manager may restrict access to localhost by default. Do not expose the Manager app publicly; if Jenkins is on another host, allow only the Jenkins agent IP.

## C. Configure Jenkins tools
Under **Manage Jenkins → Tools**, configure:
- JDK named exactly `JDK17`, pointing to JDK 17
- Maven named exactly `Maven3`

Install/enable Pipeline, Git, JUnit, Credentials Binding, and Workspace Cleanup plugins. The agent needs Git, Java 17, Maven, curl, and network access to Tomcat.

## D. Add credentials in Jenkins
Under **Manage Jenkins → Credentials → System → Global credentials → Add Credentials**:
- Kind: Username with password
- Username: `deployer`
- Password: the password configured in Tomcat
- ID: `tomcat-deployer`

Never commit the password to Git.

## E. Push this project to GitHub
Create an empty GitHub repository, then run in this folder:
```bash
git init
git add .
git commit -m "Initial online examination system"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPOSITORY.git
git push -u origin main
```
Replace the remote URL with your repository URL.

## F. Create a Jenkins Pipeline job
1. **New Item → Pipeline**, name it `Online-Examination-CICD`.
2. In Pipeline, select **Pipeline script from SCM**.
3. SCM: Git; Repository URL: your repo; Branch: `*/main`; Script Path: `Jenkinsfile`.
4. Save and click **Build Now**.
5. For a private repository, configure Git credentials in Jenkins.

## G. Trigger automatically on a push
For a reachable Jenkins server, enable **GitHub hook trigger for GITScm polling** and add a GitHub webhook to `http://YOUR_JENKINS_HOST:8080/github-webhook/` using the actual Jenkins port and a reachable endpoint. GitHub cannot reach `localhost` on your laptop.
For a local practical, use **Poll SCM** with schedule `H/2 * * * *` so Jenkins checks for changes approximately every two minutes.

## H. Verify deployment
- Landing page: `http://localhost:8080/exam/`
- Exam form: `http://localhost:8080/exam/exam`
- Health check: `http://localhost:8080/exam/health` (expected body: `OK`)

## Troubleshooting
- **JDK17/Maven3 not found:** tool names must exactly match the Jenkinsfile.
- **401 on deploy:** check credential ID and Tomcat `manager-script` role.
- **403 on deploy:** Tomcat Manager IP restrictions may block the Jenkins agent.
- **404 on health:** use Tomcat 10.1 and confirm context `/exam`.
- **Port 8080 busy:** keep Tomcat on 8080 and run Jenkins on 8081, or change `TOMCAT_URL`.
- **`sh` step unsupported:** this pipeline is written for a Linux/Unix agent. Use a Linux agent/WSL or convert shell commands to Windows `bat`.

## Scope
This is an educational demonstration. It does not include authentication, a database, exam scheduling, or proctoring.
