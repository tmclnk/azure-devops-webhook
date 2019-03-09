## Overview 
This project demonstrates a rudimentary webhook for Azure DevOps.  The notional example is one where we need to trigger emails on an email server inside a corporate DMZ. 

It is primarily composed of Java, Spring, and Jersey. The email construction system uses Freemarker templates to allow `${properties}` to be substituted into a plaintext template file. 

### Sequence

![sequence diagram](images/sequence.png)

This project uses subscription-based Service Hooks. These fire JSON payloads to a configured location. In this case, a Java WebApp hosted inside the corporate DMZ which listens to Azure Cloud network requests over HTTPS.  The Web App listens to those JSON requests and sends emails in a special proprietary format to an email address.
