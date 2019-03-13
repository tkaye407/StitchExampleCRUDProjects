
function sendTwilioMessage() {
  const twilio = stitchClient.getServiceClient(stitch.TwilioServiceClient.factory, "twilio");
  twilio.sendMessage("19145829330", "+14758975236", "Hey there friend");
}

function sendTwilioMessageUsingFunction() {
  stitchClient.callFunction("sendTwilioMessage", ["19145829330", "Hey there friend"]).then(success => {
    alert(success);
  }, (error) => {
    alert(error);
  });
}

function sendSESEmail() {
  // myAWSService is the name of the aws service you created in the stitch UI, 
  // and it is configured with a rule that allows the SendEmail action on the SES API
  const aws = stitchClient.getServiceClient(stitch.AwsServiceClient.factory, "myAWSService");

  // These are the arguments specifically for the SES service SendEmail function
  var input = {
      Destination: { ToAddresses: ["tyler.kaye@10gen.com"] },
      Message: {
          Body: { Html: { Charset: "UTF-8", Data: "Sent with JS Stitch SDK" } },
          Subject: {
              Charset: "UTF-8",
              Data: "This is a message from user " //+ stitchClient.auth.user.id
          }
      },
      Source: "tkaye407@gmail.com"
  };

  // Build the request for the AwsServiceClient to use
  const request = new stitch.AwsRequest.Builder()
    .withService("ses")
    .withAction("SendEmail")
    .withArgs(input);

  aws.execute(request.build())
     .then(result => {
       console.log(result);
     }).catch(err => {
        // handle failure
        console.log(err);
     });
}