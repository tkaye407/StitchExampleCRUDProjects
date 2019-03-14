import { Component, OnInit } from '@angular/core';

// import { Stitch, 
//   AnonymousCredential,
//   UserPasswordCredential,
//   RemoteMongoClient, 
//   BSON,
//   StitchAppClient, 
//   GoogleCredential,
// } from "/Users/tkaye/Desktop/StitchSDKs/js/packages/browser/sdk/dist/cjs";

// import {
//   AwsServiceClient, 
//   AwsRequest
// } from "/Users/tkaye/Desktop/StitchSDKs/js/packages/browser/services/aws/dist/cjs";

import { Stitch, 
  AnonymousCredential,
  UserPasswordCredential,
  RemoteMongoClient, 
  BSON,
  StitchAppClient
} from 'mongodb-stitch-browser-sdk';

import {
  AwsServiceClient, 
  AwsRequest
} from 'mongodb-stitch-browser-services-aws';

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
})
export class HomePage implements OnInit {
  stitchClient: StitchAppClient;
  result = "No results"

  constructor() { }

  ngOnInit() {
    const sampleDoc1 = '{ "int32": { "$numberInt": "10" } }';
    const ejs1 = BSON.EJSON.parse(sampleDoc1);
    console.log(ejs1)

    const sampleDoc2 = '{"AcceptRanges":"bytes","Body":{"$binary":{"base64":"aGVsbG8gdGhlcmU=","subType":"00"}},"ContentLength":{"$numberLong":"11"},"ContentType":"binary/octet-stream","LastModified":{"$date":{"$numberLong":"1551304351000"}},"Metadata":{}}';
    const ejs2 = BSON.EJSON.parse(sampleDoc2)
    console.log(ejs2)

    this.stitchClient = Stitch.initializeDefaultAppClient("stitchdocsexamples-pqwyr");
  }

  callLogin() {
    this.stitchClient.auth.loginWithCredential(new AnonymousCredential()).then(user => {
      this.result = `Successfully logged in user with id: ${user.id}`;
    }).catch(err => {
      this.result = `Failed to login anonymous user with err: ${err}`
    })
  }

  callLogout() {
    this.stitchClient.auth.logout().then(_ => {
      this.result = `Successfully logged out`;
    }).catch(err => {
      this.result = `Failed to logout with err: ${err}`
    })
  }

  callMongo() {
    let mongoClient = this.stitchClient.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
    mongoClient.db("store").collection("items").findOne().then(result => {
      this.result = `Successfully called mongo findOne(): ${JSON.stringify(result)}`; 
    }).catch(err => {
      this.result = `Failed to call mongo findOne() with error: ${err}`;
    })
  }

  callAws() {
    let awsClient = this.stitchClient.getServiceClient(AwsServiceClient.factory, "myAWSService");

    const args = { Bucket: "tklivebucket",  Key: "example2" };

    const request = new AwsRequest.Builder().withService("s3").withAction("GetObject").withArgs(args);
    awsClient.execute(request.build()).then(res => {
      this.result = `AWS Result: ${JSON.stringify(res)}`;
    }).catch(err => {
      this.result = `AWS call failed with error: ${err}`;
    })
  }

  callFunc() {
    this.stitchClient.callFunction("getString", ["string1", "otherString2"]).then(result => {
      this.result = `Successfully called stitch function: ${result}`; 
    }).catch(err => {
      this.result = `Failed to call stitch function with error: ${err}`;
    })
  }
}
