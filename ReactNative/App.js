import React from 'react'
import { Button, StyleSheet, Text, View } from 'react-native';
import { Stitch, 
    AnonymousCredential,
    RemoteMongoClient,
    BSON 
} from 'mongodb-stitch-react-native-sdk';
 
export default class App extends React.Component {
  constructor(props) {
    super(props);
    this.state={
      currentUserId: undefined,
      result: undefined, 
      client: undefined
    };
    this._onPressFind = this._onPressFind.bind(this);
    this._loadClient = this._loadClient.bind(this);
    this._onPressLogin = this._onPressLogin.bind(this);
    this._onPressLogout = this._onPressLogout.bind(this);
  }
 
  componentDidMount() {
    this._loadClient();
  }
 
  render() {
    let loginStatus = "Currently logged out."
 
    if(this.state.currentUserId) {
      loginStatus = `Currently logged in as ${this.state.currentUserId}!`
    }

    let result = "No results"
    if(this.state.result) {
      result = this.state.result
    }
 
    loginButton = <Button
                    onPress={this._onPressLogin}
                    title="Login"/>
 
    logoutButton = <Button
                    onPress={this._onPressLogout}
                    title="Logout"/>

    findButton = <Button
                    onPress={this._onPressFind}
                    title="Find"/>
 
    return (
      <View style={styles.container}>
        <Text> {loginStatus} </Text>
        {this.state.currentUserId !== undefined ? logoutButton : loginButton}
        <Text> {result} </Text>
        {findButton}
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
    this.state.client.auth.loginWithCredential(new AnonymousCredential()).then(user => {
        console.log(`Successfully logged in as user ${user.id}`);
        this.setState({ currentUserId: user.id })
    }).catch(err => {
        console.log(`Failed to log in anonymously: ${err}`);
        this.setState({ currentUserId: undefined })
    });
  }
 
  _onPressLogout() {
    this.state.client.auth.logout().then(user => {
        console.log(`Successfully logged out`);
        this.setState({ currentUserId: undefined })
    }).catch(err => {
        console.log(`Failed to log out: ${err}`);
        this.setState({ currentUserId: undefined })
    });
  }

  _onPressFind() {
    let mongoClient = this.state.client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");

    let itemsCollection = mongoClient.db("store").collection("items");

    itemsCollection.find({}, {limit: 1}).first().then(foundDoc => {
      if (foundDoc) {
        console.log(JSON.stringify(foundDoc))
        this.setState({result: JSON.stringify(foundDoc)})
      } else {
        this.setState({result: "None Found"})
      }
    }, (error) => {
      this.setState({result: "Error Finding Document"})
    });
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