

function login() {
	stitchClient.auth.loginWithCredential(new stitch.AnonymousCredential()).then(user => {
	   console.log(`Logged in as anonymous user with id: ${user.id}`);
	}, (error) => {
	    console.log(error);
	});
}

function logout() {
	stitchClient.auth.logout().then(_ => {
		console.log(`Succesffuly logged out of user`);
	}).catch(err => {
		console.log("Failed to logout of user")
	})
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

function callFunction() {
	stitchClient.callFunction("getString", ["string1", "string2"]).then(result => {
		console.log(`Successfully called stitch function: ${result}`);
	}).catch(err => {
		console.log(`Error calling stitch function: ${err}`);
	})
}