## AUTHENTICATION AND AUTHORIZATION,  ISLAND-FOX-JAVA-TRIBES

We have two methods of authentication.
In order one we have normal authentication in spring boot security. In order two we have OAuth2.
In configuration class -> security -> SecurityConfigurer. In method configure we can add any endpoint, where we want to skip authentication process with JWT token.
JWT token will be generate from /login endpoint in RegistrationController Class. The user must enter his correct username and password. JWT must be inserted in the Authentication header in Postman, where we will set the form Bearer.
OAuth2 works little bit different. 

After turning on the application, localhost will offer you the option to log in via Google or Facebook.
After successful OAuth2, client will be redirect to security -> SecurityConfigurer method configure (successHandler). The overwrite method selects the e-mail from OAuth2, sets the e-mail, username, and temporary password and save new user into the database. 
Also, users username will be add into the LIST_OAUTH_USERS on the index 0.  OAuth2 method generate his own JWT token, which is longer than our JWT generate directly by app. If user later try to use JWT token from OAuth2, JwtRequestFilter class will catch OAuth2 JWT token and switch it for our token use JwtUtil class. 

