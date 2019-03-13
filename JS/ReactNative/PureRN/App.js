import React from 'react'
import { Button, StyleSheet, Text, View } from 'react-native';
import { Stitch, 
    AnonymousCredential,
    UserPasswordCredential,
    RemoteMongoClient,
    BSON 
} from 'mongodb-stitch-react-native-sdk';

import {
  AwsServiceClient, 
  AwsRequest
} from 'mongodb-stitch-react-native-services-aws'
 
export default class App extends React.Component {
  constructor(props) {
    super(props);
    this.state={
      currentUserId: undefined,
      mongoResult: undefined, 
      awsResult: undefined,
      funcResult: undefined,
      client: undefined
    };
    this._onPressFind = this._onPressFind.bind(this);
    this._loadClient = this._loadClient.bind(this);
    this._onPressLogin = this._onPressLogin.bind(this);
    this._onPressLogout = this._onPressLogout.bind(this);
    this._onPressAWS = this._onPressAWS.bind(this);
    this._onPressFunc = this._onPressFunc.bind(this);
  }
 
  componentDidMount() {
    this._loadClient();
  }
 
  render() {
    let loginStatus = this.state.currentUserId ? `Logged in user: ${this.state.currentUserId}` : "Currently logged out.";
    let mongoResult  = this.state.mongoResult  ? this.state.mongoResult  : "No Mongo Results";
    let awsResult = this.state.awsResult ? this.state.awsResult : "No AWS Results";
    let funcResult = this.state.funcResult ? `Received from Stitch Function: ${this.state.funcResult}` : "No Stitch Function Results";

    loginButton = <Button onPress={this._onPressLogin} title="Login"/>
    logoutButton = <Button onPress={this._onPressLogout} title="Logout"/>
    findButton = <Button onPress={this._onPressFind} title="Find"/>       
    awsButton = <Button  onPress={this._onPressAWS} title="AWS GetBucket"/>
    funcButton = <Button  onPress={this._onPressFunc} title="Call Stitch Function"/>
 
    return (
      <View style={styles.container}>
        <Text> {loginStatus} </Text>
        {this.state.currentUserId !== undefined ? logoutButton : loginButton}
        <Text> {mongoResult} </Text>
        {findButton}
        <Text> {awsResult} </Text>
        {awsButton}
        <Text> {funcResult} </Text>
        {funcButton}
      </View>
    );
  }
 
  _loadClient() {
    Stitch.initializeDefaultAppClient('stitchdocsexamples-pqwyr').then(client => {
      this.setState({ client });
 
      if(client.auth.isLoggedIn) {
        this.setState({ currentUserId: client.auth.user.id })
      }
    });
  }
 
  _onPressLogin() {
    this.state.client.auth.loginWithCredential(new UserPasswordCredential("tkaye407@gmail.com", "password")).then(user => {
      console.log(`Successfully logged in as user ${user.id}`);
      this.setState({ currentUserId: user.id })
    }).catch(err => {
        console.log(`Failed to log in: ${err}`);
        this.setState({ currentUserId: undefined })
    });
  }
 
  _onPressLogout() {
    this.state.client.auth.logout().then(user => {
        console.log(`Successfully logged out`);
        this.setState({ currentUserId: undefined })
    }).catch(err => {
        console.log(`Failed to log out: ${err}`);
        this.setState({result: err.toString()})
        this.setState({ currentUserId: undefined })
    });
  }

  async _onPressFind() {
    let mongoClient = this.state.client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
    let itemsCollection = mongoClient.db("store").collection("items");
    try {
      let doc = await itemsCollection.findOne({});
      if (doc) {
        this.setState({mongoResult: JSON.stringify(doc)})
      } else {
        this.setState({mongoResult: "None Found"})
      }
    } catch (err) {
      this.setState({mongoResult: `Error Finding Document: ${err}`});
    }
  }

  _onPressAWS() {
    let awsClient = this.state.client.getServiceClient(AwsServiceClient.factory, "myAWSService");

    const args = {
      Bucket: "tklivebucket", 
      Key: "example2", 
    };

    const request = new AwsRequest.Builder().withService("s3").withAction("GetObjectss").withArgs(args);
    awsClient.execute(request.build()).then(res => {
      this.setState({ awsResult: JSON.stringify(res) })
    }).catch(err => {
      this.setState({ awsResult: err.toString() })
    })
  }

  async _onPressFunc() {
    try {
      let res = await this.state.client.callFunction("getString", ["oneString", "twoStrings"]);
      this.setState({ funcResult: res })
    } catch (err) {
      console.log("In catch");
      this.setState({ funcResult: err.toString() })
    }
  }
}
 
const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});