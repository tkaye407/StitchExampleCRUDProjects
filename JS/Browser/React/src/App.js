import React, { Component } from 'react';
import './App.css';

// import { Stitch, 
//   AnonymousCredential,
//   RemoteMongoClient, 
//   BSON,
// } from "/Users/tkaye/Desktop/StitchSDKs/js/packages/browser/sdk/dist/cjs";

// import {
//   AwsServiceClient, 
//   AwsRequest
// } from "/Users/tkaye/Desktop/StitchSDKs/js/packages/browser/services/aws/dist/cjs";

import { Stitch, 
  AnonymousCredential,
  RemoteMongoClient, 
  // BSON,
} from 'mongodb-stitch-browser-sdk';

import {
  AwsServiceClient, 
  AwsRequest
} from 'mongodb-stitch-browser-services-aws';

class App extends Component {
  constructor(props) {
    super(props);
    this.state = { result: 'Blank' };
    this.login = this.login.bind(this);
    this.logout = this.logout.bind(this);
    this.callMongo = this.callMongo.bind(this);
    this.callAWS = this.callAWS.bind(this);
    this.callFunc = this.callFunc.bind(this);
    this.stitchClient = Stitch.initializeDefaultAppClient("stitchdocsexamples-pqwyr");
  }

  render() {
    return (
      <div className="App">
        <header className="App-header">
          <p>
            Edit <code>src/App.js</code> and save to reload.
          </p>
          <button className="button" onClick={this.login}> Login </button> <br></br>
          <button className="button" onClick={this.logout}> Logout </button> <br></br>
          <button className="button" onClick={this.callMongo}> Call Mongo Remote Find </button> <br></br>
          <button className="button" onClick={this.callAWS}> Call AWS S3 GetObject </button> <br></br>
          <button className="button" onClick={this.callFunc}> Call Stitch Function </button> <br></br>
          <p> { this.state.result } </p>
        </header>
      </div>
    );
  }

  setNewResult(res) {
    this.setState(state => ({
      result: res
    }));
  }

  login() {
    console.log("HI")
    this.stitchClient.auth.loginWithCredential(new AnonymousCredential()).then(user => {
      this.setNewResult(`Successfully logged in user with id: ${user.id}`);
    }).catch(err => {
      this.setNewResult(`Failed to login anonymous user with err: ${err}`);
    })
  }

  logout() {
    this.stitchClient.auth.logout().then(_ => {
      this.setNewResult(`Successfully logged out`);
    }).catch(err => {
      this.setNewResult(`Failed to logout with err: ${err}`);
    })
  }

  callMongo() {
    let mongoClient = this.stitchClient.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
    mongoClient.db("store").collection("items").findOne().then(result => {
      this.setNewResult(`Successfully called mongo findOne(): ${JSON.stringify(result)}`); 
    }).catch(err => {
      this.setNewResult(`Failed to call mongo findOne() with error: ${err}`);
    })
  }

  callAWS() {
    let awsClient = this.stitchClient.getServiceClient(AwsServiceClient.factory, "myAWSService");

    const args = { Bucket: "tklivebucket",  Key: "example2" };

    const request = new AwsRequest.Builder().withService("s3").withAction("GetObject").withArgs(args);
    awsClient.execute(request.build()).then(res => {
      this.setNewResult(`AWS Result: ${JSON.stringify(res)}`);
    }).catch(err => {
      this.setNewResult(`AWS call failed with error: ${err}`);
    })
  }

  callFunc() {
    this.stitchClient.callFunction("getString", ["string1", "otherString2"]).then(result => {
      this.setNewResult(`Successfully called stitch function: ${result}`); 
    }).catch(err => {
      this.setNewResult(`Failed to call stitch function with error: ${err}`);
    })
  }
}

export default App;
