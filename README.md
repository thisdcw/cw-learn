1. Run the springboot Project `CwShiroApplication`
2. use postman request post `http://127.0.0.1:8080/api/login` with params username:`admin` and password:`123456`
3. you can see result is `login success`
4. but inside the class CustomRealm.java method doGetAuthenticationInfo,I created an exception in `userService.mockException`
5. so the request result is Wrong,It should fail verification.
