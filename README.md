# Azure Devops Webhooks
This project contains a Java Web Application which will receive a JSON payload from Azure DevOps and send a templated email. You can use a project like this if you need Azure DevOps subscription based services that link to services inside a corporate DMZ (e.g. JMS Queues, web services, internal Jenkins servers, messages to internal IRC channels, etc).

# Requirements
This project requires Java and Maven to run. You can also import it as a Maven
project in Eclipse and it will function as a regular Dynamic Web Application,
so you can use the "Run As > Run on Server" option context menu option.

# Run from Command Line 
```
git clone https://github.com/tmcoma/azure-devops-webhook.git
cd azure-devops-webhook
mvn clean verify
mvn cargo:run
```

This will download and run a Tomcat container at http://localhost:8080/service-portal-webhook.

You can override the default ports (note the parens around the `-D` flags).
```
mvn cargo:run "-Dcargo.servlet.port=8081" "-Dcargo.tomcat.ajp.port=8010"
```

# Build with Profiles
There are 3 maven profiles, one for each target environment: local (default), cat, and prod. For CI builds, you should also specify a revision.

```
mvn -P <targetEnvironment> -Drevision=<YYYYMMDD> clean package
```

# Troubleshooting
The integration tests here are have hard-coded project names, so the `verify` goal will fail until you put an Azure DevOps PAT in.

# Contributing
Before merging into the mastter branch, please
* Follow all [SonarLint](https://www.sonarlint.org/) recommendations
* Write [JUnit](https://junit.org/junit4/) tests.  Use maven convention of "Test" suffix for unit tests and "IT" suffix for integration tests
* Write javadoc for all nontrivial methods

# See Also
- [Adding service hooks](https://docs.microsoft.com/en-us/azure/devops/extend/develop/add-service-hook?view=vsts)
- [Webhooks](https://docs.microsoft.com/en-us/azure/devops/service-hooks/services/webhooks?view=vsts)
- [Events](https://docs.microsoft.com/en-us/azure/devops/service-hooks/events?view=vsts#workitem.updated)
- <https://docs.microsoft.com/en-us/azure/devops/extend/develop/add-service-hook?view=vsts>
- <https://docs.microsoft.com/en-us/azure/devops/service-hooks/services/webhooks?view=vsts>
- <https://docs.microsoft.com/en-us/azure/devops/service-hooks/events?view=vsts#workitem.updated>
