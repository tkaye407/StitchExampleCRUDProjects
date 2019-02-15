const pathToServerSDK = "/Users/tkaye/Desktop/StitchSDKs/js/packages/server/sdk/dist/cjs";
const {
    Stitch, 
    AnonymousCredential,
    RemoteMongoClient,
    BSON
} = require(pathToServerSDK);

const app = Stitch.initializeDefaultAppClient('stitchdocsexamples-pqwyr');

app.auth.loginWithCredential(new AnonymousCredential()).then(user => {
	console.log("Successfully logged in with anonymous user " + user.id )

    // Get a MongoDB Service Client
    const mongoClient = app.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");

    // Get a reference to the items database
    const itemsCollection = mongoClient.db("store").collection("items");
    let filter ={name: new BSON.BSONRegExp("leg", "i")}
    itemsCollection.find(filter, {limit: 1}).first().then(result => {
    	if (result) {
    		console.log("Successfully Found Document using BSON.BSONRegExp()" + JSON.stringify(result));
    	} else {
    		console.log("No matching document found");
    	}
    }, (error) => {
    	alert("Error Finding Document: " + error);
    });

    let filter2 ={name: {$regex: /leg/}}
    itemsCollection.find(filter2, {limit: 1}).first().then(result => {
    	if (result) {
    		console.log("Successfully Found Document using $regex" + JSON.stringify(result));
    	} else {
    		console.log("No matching document found");
    	}
    }, (error) => {
    	alert("Error Finding Document: " + error);
    });

    let filter3 ={name: {$regsex: new BSON.BSONRegExp("leg", "i")}}
    itemsCollection.find(filter3, {limit: 1}).first().then(result => {
    	if (result) {
    		console.log("Successfully Found Document using both" + JSON.stringify(result));
    	} else {
    		console.log("No matching document found");
    	}
    	
    }).catch(error => {
        console.log("Error Finding Document: " + error);
    });

    try {
        app.auth.switchToUserWithId("a")
    } catch(err) {
        console.log("Switch Error: " + err)
    }
}).catch(err => {
    console.log(err);
    client.close();
})
