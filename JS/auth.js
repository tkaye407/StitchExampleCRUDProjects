

function anonymousLogin() {
	stitchClient.auth.loginWithCredential(new stitch.AnonymousCredential()).then(user => {
	   console.log(`Logged in as anonymous user with id: ${user.id}`);
	}, (error) => {
	    console.log(error);
	});
}

function linkUser() {
	stitchClient.auth.user.linkWithCredential(new stitch.UserPasswordCredential("tyler.kaye@10gen.com", "password")).then(user => {
		console.log(`Successfully linked user, now ${user.loggedInProviderType}`);
	}, (error) => {
	    console.log(error);
	});
}

function reset() {
	itemsCollection.deleteMany({}).then( result => {
		console.log(result.deletedCount)
		insertOne();
		insertMany();
	});
}