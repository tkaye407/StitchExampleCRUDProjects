

function anonymousLogin() {
	stitchClient.auth.loginWithCredential(new stitch.AnonymousCredential()).then(user => {
	   console.log(`Logged in as anonymous user with id: ${user.id}`);
	}).catch(console.error);  
}