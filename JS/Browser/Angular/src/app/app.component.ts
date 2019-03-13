import { Component, OnInit } from '@angular/core';

import { Stitch, 
  AnonymousCredential,
  UserPasswordCredential,
  RemoteMongoClient, 
  BSON,
  StitchAppClient
} from "/Users/tkaye/Desktop/StitchSDKs/js/packages/browser/sdk/dist/cjs";

import {
  AwsServiceClient, 
  AwsRequest
} from "/Users/tkaye/Desktop/StitchSDKs/js/packages/browser/services/aws/dist/cjs";

// import { Stitch, 
//   AnonymousCredential,
//   UserPasswordCredential,
//   RemoteMongoClient, 
//   BSON,
//   StitchAppClient
// } from 'mongodb-stitch-browser-sdk';

// import {
//   AwsServiceClient, 
//   AwsRequest
// } from 'mongodb-stitch-browser-services-aws';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  stitchClient: StitchAppClient;
  title = 'Sample';
  funcResult = "No Function Result";
  awsResult = "No AWS Result";
  mongoResult = "No Mongo Result";
  loginResult = "No Login Result";
  logoutResult = "No Logout Result";

  constructor() { }

  ngOnInit() {
    this.title = "MongoDB Stitch Sample App"

    const sampleDoc1 = '{ "int32": { "$numberInt": "10" } }';
    const ejs1 = BSON.EJSON.parse(sampleDoc1);
    console.log(ejs1)

    const sampleDoc2 = '{"AcceptRanges":"bytes","Body":{"$binary":{"base64":"aGVsbG8gdGhlcmU=","subType":"00"}},"ContentLength":{"$numberLong":"11"},"ContentType":"binary/octet-stream","LastModified":{"$date":{"$numberLong":"1551304351000"}},"Metadata":{}}';
    const ejs2 = BSON.EJSON.parse(sampleDoc2)
    console.log(ejs2)

    this.stitchClient = Stitch.initializeDefaultAppClient("stitchdocsexamples-pqwyr");
  }

  login() {
    this.stitchClient.auth.loginWithCredential(new AnonymousCredential()).then(user => {
      this.loginResult = `Successfully logged in user with id: ${user.id}`;
      this.logoutResult = "NA";
    }).catch(err => {
      this.loginResult = `Failed to login anonymous user with err: ${err}`
    })
  }

  logout() {
    this.stitchClient.auth.logout().then(_ => {
      this.logoutResult = `Successfully logged out`;
      this.loginResult = "NA";
    }).catch(err => {
      this.logoutResult = `Failed to logout with err: ${err}`
    })
  }

  callMongo() {
    let mongoClient = this.stitchClient.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
    mongoClient.db("store").collection("items").findOne().then(result => {
      this.mongoResult = `Successfully called mongo findOne(): ${JSON.stringify(result)}`; 
    }).catch(err => {
      this.mongoResult = `Failed to call mongo findOne() with error: ${err}`;
    })
  }

  callAWS() {
    let awsClient = this.stitchClient.getServiceClient(AwsServiceClient.factory, "myAWSService");

    const args = { Bucket: "tklivebucket",  Key: "example2" };

    const request = new AwsRequest.Builder().withService("s3").withAction("GetObject").withArgs(args);
    awsClient.execute(request.build()).then(res => {
      this.awsResult = `AWS Result: ${JSON.stringify(res)}`;
    }).catch(err => {
      this.awsResult = `AWS call failed with error: ${err}`;
    })
  }

  callFunc() {
    this.stitchClient.callFunction("getString", ["string1", "otherString2"]).then(result => {
      this.funcResult = `Successfully called stitch function: ${result}`; 
    }).catch(err => {
      this.funcResult = `Failed to call stitch function with error: ${err}`;
    })
  }
}
