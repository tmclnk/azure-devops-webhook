# Configuration 
This page is intended to serve as a guide to installing service hooks on the Azure DevOps side. It does not address how to configure the Java hosted portion of the application.

### Prerequisites

1. **Project Administrator** or **Project Collection Administrator** access to an Azure DevOps Project 
1. An API Key. 
1. The Service Portal Support Group name you wish direct your tickets to


### Steps
As a **Project Administrator**, go to **Project Settings**, select **Service Hooks** and click **Create Subscription**

![create-sub](images/create-subscription.png)

On the next screen, select **Work Item Created** and the type of work item you wish to trigger based on.  


![add-url](images/add-url.png)


In the URL Field, enter the URL you wish to use.  This will be of the form

https://devops-hooks-prod.example.com/webhook/serviceportalv1/email/**SUPPORT_GROUP**/onCreate


You can leave the username blank and enter the API Token you have been provided.  Leave all the other dropdowns as their default values, but make sure **RESOURCE VERSION 1.0** is specified.

### Overriding the Target Email
If you need to troubleshoot or alter the outgoing email, pass an additional header `orchestrator.email` set to the email address you wish to send to.