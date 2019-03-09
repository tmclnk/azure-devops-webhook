# Security
Authentication to the service is just [Basic Auth](https://en.wikipedia.org/wiki/Basic_access_authentication) with a blank username and an API Key. 

# API Keys
API keys are 40-character strings stored in a file deployed to the server in a file called `api-tokens.txt`. There is no revocation or tracking mechanism on these.


# Username/Password Basic Auth
Prototype versions of this service also used `api-users.properties`, which had separate usernames and passwords.  This is considered deprecated.

# Roadmap
Storing all of this stuff in plaintext is a concern.  See the [Issue Tracker](issue-management.html) for details. When [bikeshedding](https://en.wiktionary.org/wiki/bikeshedding) this project into oblivion, remember that API keys are added once per project into Azure DevOps, then not dealt with again.

If you wish to update this project and do a key rotation scheme, you will need to also address how to keep API Consumers up to date (which is do-able through the API). 
