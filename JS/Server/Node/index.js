const {
    Stitch, 
    AnonymousCredential,
    RemoteMongoClient,
    BSON
} = require('mongodb-stitch-server-sdk');

const {
    AwsRequest, 
    AwsServiceClient
} = require("mongodb-stitch-server-services-aws");

// const {
//     Stitch, 
//     AnonymousCredential,
//     RemoteMongoClient,
//     BSON
// } = require("/Users/tkaye/Desktop/StitchSDKs/js/packages/server/sdk/dist/cjs");

// const {
//     AwsRequest, 
//     AwsServiceClient
// } = require("/Users/tkaye/Desktop/StitchSDKs/js/packages/server/services/aws/dist/cjs");

const stitchClient = Stitch.initializeDefaultAppClient('stitchdocsexamples-pqwyr');

stitchClient.auth.loginWithCredential(new AnonymousCredential()).then(user => {
	console.log("Successfully logged in with anonymous user " + user.id )

    // Get a MongoDB Service Client
    const mongoClient = stitchClient.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");

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
        console.log("*** Error Finding Document: " + error);
        process.exit(0);    
    });

    let filter2 ={name: {$regex: /leg/}}
    itemsCollection.find(filter2, {limit: 1}).first().then(result => {
    	if (result) {
    		console.log("Successfully Found Document using $regex" + JSON.stringify(result));
    	} else {
    		console.log("No matching document found");
    	}
    }, (error) => {
    	console.log("*** Error Finding Document: " + error);
        process.exit(0);
    });

    let filter3 ={name: {$regex: new BSON.BSONRegExp("leg", "i")}}
    itemsCollection.find(filter3, {limit: 1}).first().then(result => {
    	if (result) {
    		console.log("Successfully Found Document using both" + JSON.stringify(result));
    	} else {
    		console.log("No matching document found");
    	}
    	
    }, (error) => {
    	console.log("*** Error Finding Document: " + error);
        process.exit(0);
    });


    // Call AWS: 
    let awsClient = stitchClient.getServiceClient(AwsServiceClient.factory, "myAWSService");

    const args = { Bucket: "tklivebucket",  Key: "example2" };

    const request = new AwsRequest.Builder().withService("s3").withAction("GetObject").withArgs(args);
    awsClient.execute(request.build()).then(res => {
        console.log(`Successfully called AWS GetObject: ${JSON.stringify(res)}`);
    }).catch(err => {
        console.log(`*** AWS call failed with error: ${err}`);
        process.exit(0);
    })

    // Call Function: 
    stitchClient.callFunction("getString", ["string1", "otherString2"]).then(result => {
        console.log(`Successfully called stitch function: ${result}`); 
    }).catch(err => {
        console.log(`*** Failed to call stitch function with error: ${err}`);
        process.exit(0);
    })


    // Logout
    setTimeout(function() {
        console.log('Logging out....');
        stitchClient.auth.logout().then(_ => {
            itemsCollection.findOne().then(_ => {

            }).catch(err => {
                
            })
        }).catch(err => {
            console.log(`*** Failed to call logout with error: ${err}`);
        process.exit(0);
        })
    }, 2000);

}).catch(err => {
    console.log("*** ERROR:" + err);
    client.close();
    process.exit(0);
})