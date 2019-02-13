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
    
    private lazy var stitchClient = Stitch.defaultAppClient!
    private var mongoClient: RemoteMongoClient?
    private var itemsCollection: RemoteMongoCollection<Document>?

    override func viewDidLoad() {
        super.viewDidLoad()
        // Set the stitch variables declared above for use below
        mongoClient = stitchClient.serviceClient(fromFactory: remoteMongoClientFactory, withName: "mongodb-atlas")
        itemsCollection = mongoClient?.db("store").collection("items")
        anonymousLogin()
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    // Function called on button click to send email
    @IBAction func sendMessageClicked(_ sender: Any) {
        // insertOne();
        // insertMany();
        
        // findOne();
        // findMany();
        
        // deleteOne();
        // deleteMany();
        
        // updateChangeFieldAndAddField();
        // upsertOne();
        // updatePushArrayOfSubdocuments();
        // updateMany();
        
        aggregate();
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
     * INSERT                                                                  *
     ***************************************************************************/
    func insertOne() {
        let doc: Document = [
            "name": "legos",
            "quantity": 10,
            "category": "toys",
            "reviews": [["username": "mongolover", "comment": "this is great"] as Document],
            ];
        
        itemsCollection?.insertOne(doc) { result in
            switch result {
            case .success(let result):
                print("Successfully Inserted Document with ID: \(result.insertedId))");
            case .failure(let error):
                print("Failed to Insert Document with Error: \(error)");
            }
        }
    }
    
    func insertMany() {
        let doc1: Document = ["name": "basketball", "category": "sports", "quantity": 20, "reviews": []];
        let doc2: Document = ["name": "football", "category": "sports", "quantity": 30, "reviews": []];
        
        itemsCollection?.insertMany([doc1, doc2]) { result in
            switch result {
            case .success(let result):
                print("Successfully Inserted Documents with IDs: \(result.insertedIds))");
            case .failure(let error):
                print("Failed to Insert Documents with Error: \(error)");
            }
        }
    }
    
    /***************************************************************************
     * FIND                                                                    *
     ***************************************************************************/
    // Find one item with a quantity greater than or equal to 25
    func findOne() {
        let filterDoc : Document = ["quantity": ["$gte": 25] as Document];
        let options = RemoteFindOptions(limit: 1);
        
        itemsCollection?.find(filterDoc, options: options).first({ result in
            switch result {
            case .success(let result):
                print("Successfully Found Document: \(result?.description ?? "None Found")");
            case .failure(let error):
                print("Failed to Find Document with Error: \(error)");
            }
        })
    }
    
    // Find all items with at least one review and sort by name and hide the _id
    func findMany() {
        //let filterDoc : Document = ["reviews": ["$size": ["$gte": 1] as Document] as Document];
        let filterDoc : Document = ["reviews.0": ["$exists": true] as Document];
        let options = RemoteFindOptions(projection: ["_id": 0], sort: ["name": 1]);
        
        itemsCollection?.find(filterDoc, options: options).toArray({ results in
            switch results {
            case .success(let results):
                print("Successfully Found \(results.count) documents: ");
                results.forEach({item in
                    print(item);
                })
            case .failure(let error):
                print("Failed to Find Documents with Error: \(error)");
            }
        })
    }
    
    /***************************************************************************
     * DELETE                                                                  *
     ***************************************************************************/
    
    // Delete an item with the name "legos" and only one
    func deleteOne() {
        let filterDoc : Document = ["name": "legos"];
        
        itemsCollection?.deleteOne(filterDoc) { result in
            switch result {
            case .success(let result):
                print("Successfully Deleted \(result.deletedCount) documents.");
            case .failure(let error):
                print("Failed to Delete Documents with Error: \(error)");
            }
        }
    }
    
    // Delete all items without any reviews
    func deleteMany() {
        let filterDoc : Document = ["reviews": ["$size": 0] as Document];
        
        itemsCollection?.deleteMany(filterDoc) { result in
            switch result {
            case .success(let result):
                print("Successfully Deleted \(result.deletedCount) documents.");
            case .failure(let error):
                print("Failed to Delete Documents with Error: \(error)");
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
    
    // Upsert one document
    func upsertOne() {
        let filterDoc : Document = ["name": "board game"];
        let updateDoc: Document = ["$inc": ["quantity": 5] as Document];
        let options = RemoteUpdateOptions(upsert: true);
        
        itemsCollection?.updateOne(filter: filterDoc, update: updateDoc, options: options) { result in
            switch result {
            case .success(let result):
                if let upsertid = result.upsertedId {
                    print("Successfully upserted document with id: \(upsertid).");
                } else {
                    print("Update Successful, matched: \(result.matchedCount), modified: \(result.modifiedCount) documents.");
                }
            case .failure(let error):
                print("Failed to Update Document with Error: \(error)");
            }
        }
    }
    
    
    
    // Update document by adding document to array of subdocuments
    func updatePushArrayOfSubdocuments() {
        let filterDoc : Document = ["name": "football"];
        let updateDoc : Document = ["$push":
            ["reviews": [
                "username": "stitchfan2018", "comment": "what a neat product"
            ] as Document
        ] as Document];
        
        itemsCollection?.updateOne(filter: filterDoc, update: updateDoc) { result in
            switch result {
            case .success(let result):
                print("Update Successful, matched: \(result.matchedCount), modified: \(result.modifiedCount) documents.");
            case .failure(let error):
                print("Failed to Update Document with Error: \(error)");
            }
        }
    }
    
    // Update many documents
    func updateMany() {
        let filterDoc : Document = [];
        let updateDoc : Document = ["$mul": ["quantity": 10] as Document];
        
        itemsCollection?.updateMany(filter: filterDoc, update: updateDoc) { result in
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

