//
//  ViewController.swift
//  StitchExamples
//
//  Created by Tyler Kaye on 12/11/18.
//  Copyright © 2018 Tyler Kaye. All rights reserved.
//

import UIKit

//Imports
import StitchCore
import StitchAWSService
import StitchCoreAWSService
import StitchTwilioService
import StitchCoreRemoteMongoDBService
import StitchRemoteMongoDBService

class ViewController: UIViewController {
    @IBOutlet weak var output: UITextView!
    
    private lazy var stitchClient = Stitch.defaultAppClient!
    private var mongoClient: RemoteMongoClient?
    private var itemsCollection: RemoteMongoCollection<Document>?

    override func viewDidLoad() {
        super.viewDidLoad()
        // Set the stitch variables declared above for use below
        mongoClient = stitchClient.serviceClient(fromFactory: remoteMongoClientFactory, withName: "mongodb-atlas")
        itemsCollection = mongoClient?.db("store").collection("items")
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    @IBAction func loginClicked(_ sender: Any) {
        // Get the default AppClient
        let stitchClient = Stitch.defaultAppClient!;
        
        // Login with anonymous credentials
        stitchClient.auth.login(withCredential: AnonymousCredential()) { result in
            switch result {
            case .success(let user):
                let text = "Successfully authenticated with user: \(user.id)";
                DispatchQueue.main.async() {
                    self.output.text = text;
                }
            case .failure(let error):
                DispatchQueue.main.async() {
                    self.output.text = "Failed to auth with error: \(error)";
                }
            }
        }
    }
    
    @IBAction func logoutClicked(_ sender: Any) {
        // Get the default AppClient
        let stitchClient = Stitch.defaultAppClient!;
        stitchClient.auth.logout { result in
            switch result {
            case .success( _):
                DispatchQueue.main.async() {
                    self.output.text = "Successfully logged out";
                }
            case .failure(let error):
                DispatchQueue.main.async() {
                    self.output.text = "Failed to logout with error: \(error)";
                }
            }
        }
    }
    
    @IBAction func callFunctionClicked(_ sender: Any) {
        let stitchClient = Stitch.defaultAppClient!;
        stitchClient.callFunction(withName: "getString", withArgs: ["string1", "string2"]) { (result: StitchResult<String>) in
            print(result)
            switch result {
            case .success(let res):
                DispatchQueue.main.async() {
                    self.output.text = "Successfully called stitch function: \(res)";
                }
            case .failure(let error):
                DispatchQueue.main.async() {
                    self.output.text = "Failed to call stitch function with error: \(error)";
                }
            }
        }
        
    }
    
    @IBAction func awsRequestClicked(_ sender: Any) {
        // Get the default AppClient
        let stitchClient = Stitch.defaultAppClient!;
        
        // myAWSService is the name of the aws service you created in the stitch UI,
        let aws = stitchClient.serviceClient(fromFactory: awsServiceClientFactory, withName: "myAWSService")
        
        let args: Document = [
            "Bucket": "tklivebucket",
            "Key": "example2"
        ];
        
        
        do {
            // Build the request object for SendEmail function in SES service
            let request = try AWSRequestBuilder()
                .with(service: "s3")
                .with(action: "GetObject")
                .with(arguments: args)
                .build()
            
            // Execute the function and handle the result
            aws.execute(request: request, {(result: StitchResult<Document>) in
                switch result {
                case .success(let result):
                    DispatchQueue.main.async() {
                        self.output.text = "Successfully called aws function: \(String(describing: result))";
                    }
                case .failure(let error):
                    DispatchQueue.main.async() {
                        self.output.text = "Failed to call aws GetObject with error: \(String(describing: error))";
                    }
                }
            })
        } catch {
            print("Failed to Send Email");
        }
    }
    
    @IBAction func inserOneClicked(_ sender: Any) {
        let doc: Document = [
            "name": "legos",
            "quantity": 10,
            "category": "toys",
            "reviews": [["username": "mongolover", "comment": "this is great"] as Document],
            ];
        
        itemsCollection?.insertOne(doc) { result in
            switch result {
            case .success(let result):
                let text = "Inserted Document with id: \(result.insertedId)\n\n\(doc)";
                DispatchQueue.main.async() {
                    self.output.text = text;
                }
            case .failure(let error):
                DispatchQueue.main.async() {
                    self.output.text = "Failed to Insert Document with Error: \(error)";
                }
            }
        }
    }
    
    @IBAction func insertManyClicked(_ sender: Any) {
        let doc1: Document = ["name": "basketball", "category": "sports", "quantity": 20, "reviews": []];
        let doc2: Document = ["name": "football", "category": "sports", "quantity": 30, "reviews": []];
        
        itemsCollection?.insertMany([doc1, doc2]) { result in
            switch result {
            case .success(let result):
                DispatchQueue.main.async() {
                    self.output.text = "Inserted Documents with ids: \(result.insertedIds)\n\n\([doc1, doc2])";
                }
            case .failure(let error):
                DispatchQueue.main.async() {
                    self.output.text = "Failed to Insert Document with Error: \(error)";
                }            }
        }
    }
    
    @IBAction func deleteOneClicked(_ sender: Any) {
        let filterDoc : Document = ["name": "legos"];
        
        itemsCollection?.deleteOne(filterDoc) { result in
            switch result {
            case .success(let result):
                let text = "Successfully Deleted \(result.deletedCount) documents with filter \(filterDoc)";
                DispatchQueue.main.async() {
                    self.output.text = text;
                }
            case .failure(let error):
                DispatchQueue.main.async() {
                    self.output.text = "Failed to Delete Document with Error: \(error)";
                }
            }
        }
    }
    
    @IBAction func deleteeManyClicked(_ sender: Any) {
        let filterDoc : Document = ["reviews": ["$size": 0] as Document];
        
        itemsCollection?.deleteMany(filterDoc) { result in
            switch result {
            case .success(let result):
                let text = "Successfully Deleted \(result.deletedCount) documents with filter \(filterDoc)";
                DispatchQueue.main.async() {
                    self.output.text = text;
                }
            case .failure(let error):
                DispatchQueue.main.async() {
                    self.output.text = "Failed to Delete Document with Error: \(error)";
                }
            }
        }
    }
    
    @IBAction func findOneClicked(_ sender: Any) {
        let filterDoc : Document = ["quantity": ["$gte": 25] as Document];
        let options = RemoteFindOptions(limit: 1);
        
        itemsCollection?.find(filterDoc, options: options).first({ result in
            switch result {
            case .success(let result):
                let text = "Successfully found document with filter: \(filterDoc)\n\n\(result?.description ?? "None Found")";
                DispatchQueue.main.async() {
                    self.output.text = text;
                }
                print("Successfully Found Document: \(result?.description ?? "None Found")");
            case .failure(let error):
                let text = "Failed to Find Document with Error: \(error)";
                DispatchQueue.main.async() {
                    self.output.text = text;
                }
            }
        })
    }
    
    @IBAction func findManyClicked(_ sender: Any) {
        let filterDoc : Document = ["reviews.0": ["$exists": true] as Document];
        let options = RemoteFindOptions(projection: ["_id": 0], sort: ["name": 1]);
        
        itemsCollection?.find(filterDoc, options: options).toArray({ results in
            switch results {
            case .success(let results):
                var text = "Successfully found \(results.count) documents with filter: \(filterDoc)\n\n";
                for result in results {
                    text += "\(result)\n";
                }
                DispatchQueue.main.async() {
                    self.output.text = text;
                }
            case .failure(let error):
                let text = "Failed to Find Documents with Error: \(error)";
                DispatchQueue.main.async() {
                    self.output.text = text;
                }
            }
        })
    }
    
    @IBAction func updateOneClicked(_ sender: Any) {
        let filterDoc : Document = ["name": "board game"];
        let updateDoc: Document = ["$inc": ["quantity": 5] as Document];
        let options = RemoteUpdateOptions(upsert: true);
        
        itemsCollection?.updateOne(filter: filterDoc, update: updateDoc, options: options) { result in
            switch result {
            case .success(let result):
                var text = "";
                if let upsertid = result.upsertedId {
                    text += "Successfully upserted document with id: \(upsertid)."
                } else {
                    text += "Update Successful, matched: \(result.matchedCount), modified: \(result.modifiedCount) documents.";
                }
                text += "\n\nFilter: \(filterDoc)\nUpdate: \(updateDoc)"
                DispatchQueue.main.async() {
                    self.output.text = text;
                }
            case .failure(let error):
                let text = "Failed to Update Documents with Error: \(error)";
                DispatchQueue.main.async() {
                    self.output.text = text;
                }
            }
        }
    }
    
    @IBAction func updateOnePushClicked(_ sender: Any) {
        let filterDoc : Document = ["name": "football"];
        let updateDoc : Document = ["$push":
            ["reviews": [
                "username": "stitchfan2018", "comment": "what a neat product"
                ] as Document
                ] as Document
        ];
        
        itemsCollection?.updateOne(filter: filterDoc, update: updateDoc) { result in
            switch result {
            case .success(let result):
                var text = "Update Successful, matched: \(result.matchedCount), modified: \(result.modifiedCount) documents.";
                text += "\n\nFilter: \(filterDoc)\nUpdate: \(updateDoc)";
                DispatchQueue.main.async() {
                    self.output.text = text;
                }
            case .failure(let error):
                let text = "Failed to Update Documents with Error: \(error)";
                DispatchQueue.main.async() {
                    self.output.text = text;
                }
            }
        }
    }
    
    @IBAction func updateManyClicked(_ sender: Any) {
        let filterDoc : Document = [];
        let updateDoc : Document = ["$mul": ["quantity": 10] as Document];
        
        itemsCollection?.updateMany(filter: filterDoc, update: updateDoc) { result in
            switch result {
            case .success(let result):
                var text = "Update Successful, matched: \(result.matchedCount), modified: \(result.modifiedCount) documents.";
                text += "\n\nFilter: \(filterDoc)\nUpdate: \(updateDoc)";
                DispatchQueue.main.async() {
                    self.output.text = text;
                }
            case .failure(let error):
                let text = "Failed to Update Documents with Error: \(error)";
                DispatchQueue.main.async() {
                    self.output.text = text;
                }
            }
        }
    }
    
    // Function called on button click to send email
    @IBAction func sendMessageClicked(_ sender: Any) {
        itemsCollection?.find([]).toArray({ results in
            switch results {
            case .success(let results):
                var text = "All Documents\n\n";
                for result in results {
                    text += "\(result)\n\n";
                }
                DispatchQueue.main.async() {
                    self.output.text = text;
                }
            case .failure(let error):
                let text = "Failed to Find Documents with Error: \(error)";
                DispatchQueue.main.async() {
                    self.output.text = text;
                }
            }
        })
    }
    
    
    /***************************************************************************
     * AUTHENTICATION                                                          *
     ***************************************************************************/
    func anonymousLogin() {
        // Get the default AppClient
        let stitchClient = Stitch.defaultAppClient!;
        
        // Login with anonymous credentials
        stitchClient.auth.login(withCredential: AnonymousCredential()) { result in
            switch result {
            case .success(let user):
                print("logged in anonymous as user \(user.id)");
            case .failure(let error):
                print("Failed to log in: \(error)");
            }
        }
    }
    
    /***************************************************************************
     * UPDATE                                                                  *
     ***************************************************************************/
    
    // Update an item to have a new name and add a new field
    func updateChangeFieldAndAddField() {
        let filterDoc : Document = ["name": "legos"];
        let updateDoc : Document = ["$set": ["name": "blocks", "price": 20.99] as Document];
        
        itemsCollection?.updateOne(filter: filterDoc, update: updateDoc) { result in
            switch result {
            case .success(let result):
                print("Update Successful, matched: \(result.matchedCount), modified: \(result.modifiedCount) documents.");
            case .failure(let error):
                print("Failed to Update Document with Error: \(error)");
            }
        }
    }
    
    /***************************************************************************
     * AGGREGATE                                                               *
     ***************************************************************************/
    // Aggregate by grouping on category and summing the quantity fields
    func aggregate() {
        let aggregationPipeline : [Document] = [
            ["$group":
                [
                    "_id": "$category",
                    "totalQuantity": ["$sum": "$quantity"] as Document,
                    "count": ["$sum": 1] as Document
                ] as Document
            ] as Document
        ];
        
        itemsCollection?.aggregate(aggregationPipeline).toArray({ results in
            switch results {
            case .success(let results):
                print("Successfull Aggregation with \(results.count) results: ");
                results.forEach({item in
                    print(item);
                })
            case .failure(let error):
                print("Failed to Perform Aggregation with Error: \(error)");
            }
        })
    }
    
    /***************************************************************************
     * EXTERNAL SERVICES                                                       *
     ***************************************************************************/
    func sendTwilioSMS() {
        // Get the default AppClient
        let stitchClient = Stitch.defaultAppClient!;
        
        // myTwilioService is the name of the Twilio service you created in the stitch UI,
        // and it is configured with a rule that allows the SendMessage action
        let twilio = stitchClient.serviceClient(fromFactory: twilioServiceClientFactory, withName: "twilio");
        
        // Compose the SMS Message
        let sourceNumber = "+14758975236";
        let destNumber = "19145829330";
        let message = "This message is sent on Twilio from iOS SDK";
        
        // Send the message using the myTwilioService and handle the result
        twilio.sendMessage(to: destNumber, from: sourceNumber, body: message, mediaURL: nil, {(result: StitchResult<Void>) in
            switch result {
            case .success(_):
                print("Sending SMS from Twilio Succeeded");
            case .failure(let error):
                print("Error sending Twilio SMS: \(String(describing: error))")
            }
        })
    }
    
    func sendSESEmail() {
        // Get the default AppClient
        let stitchClient = Stitch.defaultAppClient!;
        
        // myAWSService is the name of the aws service you created in the stitch UI,
        // and it is configured with a rule that allows the SendEmail action on the SES API
        let aws = stitchClient.serviceClient(fromFactory: awsServiceClientFactory, withName: "myAWSService")
        
        // Compose the Email Message
        let sourceEmail = "tkaye407@gmail.com";
        let destEmail   = "tyler.kaye@10gen.com";
        let message = "Hey there friend!";
        let subject = "This is a message from: \(stitchClient.auth.currentUser?.id ?? "No User")";
        let args: Document = [
            "Destination": ["ToAddresses": [destEmail]] as Document,
            "Source": sourceEmail,
            "Message": [
                "Body": [
                    "Html": ["Charset": "UTF-8", "Data": message] as Document
                    ] as Document,
                "Subject": ["Charset": "UTF-8", "Data": subject] as Document
                ] as Document
        ];
        
        
        do {
            // Build the request object for SendEmail function in SES service
            let request = try AWSRequestBuilder()
                .with(service: "ses")
                .with(action: "SendEmail")
                .with(arguments: args)
                .build()
            
            // Execute the function and handle the result
            aws.execute(request: request, {(result: StitchResult<Document>) in
                switch result {
                case .success(let result):
                    print("SendEmailSucceeded with MessageID: \(String(describing: result["MessageId"]))")
                case .failure(let error):
                    print("Error sending SES Email: \(String(describing: error))")
                }
            })
        } catch {
            print("Failed to Send Email");
        }
    }
}

