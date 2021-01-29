### یه کتابخونه کوچک واسه ای پی آی خطوط خدماتی اشتراکی سامانه ملی پیامک

------------


### Usage:
```java
MeliPayamak meliPayamak = new MeliPayamak("user name", "password", bodyId);
try {
	meliPayamak.send("test", "09120000000", new OnMessageSent() {

		@Override
		public void onSuccess(JSONObject object) {
			System.out.println("sent");
		}

		@Override
		public void onFailed(JSONObject object) {
			System.out.println("failed");
		}
	});
} catch (IOException e) {
	e.printStackTrace();
}
```
