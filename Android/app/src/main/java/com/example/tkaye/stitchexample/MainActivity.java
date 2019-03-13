package com.example.tkaye.stitchexample;

import android.widget.Button;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Stitch-Related Imports
import com.google.android.gms.tasks.Tasks;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
import com.mongodb.stitch.core.auth.providers.userpassword.UserPasswordCredential;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteDeleteResult;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertManyResult;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;


// Imports for RemoteMongo
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

// Imports for AWS
import com.mongodb.stitch.android.services.aws.AwsServiceClient;
import com.mongodb.stitch.core.services.aws.AwsRequest;

// Imports for Twilio
import com.mongodb.stitch.android.services.twilio.TwilioServiceClient;

// Non-Stitch Imports
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import org.bson.Document;


public class MainActivity extends AppCompatActivity {

    Button sendMessageButton;
    Button userpassLoginButton;
    Button anonLoginButton;
    Button removeAllUsersButton;
    private StitchAppClient stitchClient;
    private RemoteMongoClient mongoClient;
    private RemoteMongoCollection itemsCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendMessageButton = (Button) findViewById(R.id.sendMessageButton);
        anonLoginButton = (Button) findViewById(R.id.anonLoginButton);
        userpassLoginButton = (Button) findViewById(R.id.userpassLoginButton);
        removeAllUsersButton = (Button) findViewById(R.id.removeAllUsersButton);


//        anonymousLogin();

        stitchClient = Stitch.getDefaultAppClient();
        mongoClient = stitchClient.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
        itemsCollection = mongoClient.getDatabase("store").getCollection("items");









        anonLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("app", String.format("num users before: %d",
                        stitchClient.getAuth().listUsers().size()));
                try {
                    Tasks.await(stitchClient.getAuth().loginWithCredential(new AnonymousCredential()));
                } catch (Exception e) {

                }
                Log.d("app", String.format("num users after: %d",
                        stitchClient.getAuth().listUsers().size()));
                listUsers();
            }
        });

      removeAllUsersButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          removeAllUsers();
          Log.d("app", String.format("num users after: %d",
                  stitchClient.getAuth().listUsers().size()));
          listUsers();
        }
      });

        userpassLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("app", String.format("num users before=: %d",
                        stitchClient.getAuth().listUsers().size()));

              // Login with Anonymous credentials and handle the result
              stitchClient.getAuth().loginWithCredential(new UserPasswordCredential("tkaye407@gmail.com", "password")).addOnCompleteListener(new OnCompleteListener<StitchUser>() {
                @Override
                public void onComplete(@NonNull final Task<StitchUser> task) {
                  if (task.isSuccessful()) {
                    Log.d("app", String.format("num users after: %d",
                            stitchClient.getAuth().listUsers().size()));
                    listUsers();
                  } else {
                    Log.e("app", "failed to log in", task.getException());
                  }
                }
              });
            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // insertOne();
                // insertMany();

                // findOne();
                // findMany();

                // deleteOne();
                // deleteMany();

                // updateOneAddField();
                // updateOnePush();
                // updateOneUpsert();
                // updateMany();

                 aggregate();

                // sendTwilioSMS();
                // sendSESEmail();

                // anonymousLogin();
            }
        });
    }

    public void listUsers() {
        for (StitchUser user : stitchClient.getAuth().listUsers()) {
            Log.d("app", String.format("%s %s", user.getId(), user.getLoggedInProviderType()));
        }
    }

    public void removeAllUsers() {
        for (StitchUser user: stitchClient.getAuth().listUsers()) {
            stitchClient.getAuth().removeUserWithId(user.getId());
        }
    }


    /***************************************************************************
     * INSERT                                                                  *
     ***************************************************************************/
    public void insertOne() {
        Document doc = new Document()
                .append("name", "legos")
                .append("quantity", 10)
                .append("category", "toys")
                .append("reviews", Arrays.asList(
                        new Document()
                                .append("username", "mongolover")
                                .append("comment", "this is great")
                ));


        final Task<RemoteInsertOneResult> insertTask = itemsCollection.insertOne(doc);
        insertTask.addOnCompleteListener(new OnCompleteListener<RemoteInsertOneResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteInsertOneResult> task) {
                if (task.isSuccessful()) {
                    Log.d("app", String.format("successfully inserted item with id %s",
                            task.getResult().getInsertedId()));
                } else {
                    Log.e("app", "failed to insert document with: ", task.getException());
                }
            }
        });
    }

    public void insertMany() {
        Document doc1 = new Document()
                .append("name", "basketball")
                .append("category", "sports")
                .append("quantity", 20)
                .append("reviews", Arrays.asList());

        Document doc2 = new Document()
                .append("name", "football")
                .append("category", "sports")
                .append("quantity", 30)
                .append("reviews", Arrays.asList());

        List<Document> docs = Arrays.asList(doc1, doc2);

        final Task<RemoteInsertManyResult> insertTask = itemsCollection.insertMany(docs);
        insertTask.addOnCompleteListener(new OnCompleteListener<RemoteInsertManyResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteInsertManyResult> task) {
                if (task.isSuccessful()) {
                    Log.d("app",
                            String.format("successfully inserted %d items with ids: %s",
                                    task.getResult().getInsertedIds().size(),
                                    task.getResult().getInsertedIds().toString()));
                } else {
                    Log.e("app", "failed to inserts document with: ", task.getException());
                }
            }
        });
    }


    /***************************************************************************
     * FIND                                                                    *
     ***************************************************************************/
    public void findOne() {
        Document filterDoc = new Document()
                .append("quantity", new Document().append("$gte", 25));

        final Task<Document> findTask = itemsCollection.find(filterDoc).limit(1).first();
        findTask.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task<Document> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() == null) {
                        Log.d("app", "Could not find any matching documents");
                    } else {
                        Log.d("app", String.format("successfully found document: %s", task.getResult().toString()));
                    }
                } else {
                    Log.e("app", "failed to find document with: ", task.getException());
                }
            }
        });
    }

    public void findMany() {
        Document filterDoc = new Document()
                .append("reviews.0", new Document().append("$exists", true));

        RemoteFindIterable findResults = itemsCollection
                .find(filterDoc)
                .projection(new Document().append("_id", 0))
                .sort(new Document().append("name", 1));

        // One way to iterate through
        findResults.forEach(item -> {
            Log.d("app", String.format("successfully found:  %s", item.toString()));
        });

        // Another way to iterate through
        Task<List<Document>> itemsTask = findResults.into(new ArrayList<Document>());
        itemsTask.addOnCompleteListener(new OnCompleteListener<List<Document>>() {
            @Override
            public void onComplete(@NonNull Task<List<Document>> task) {
                if (task.isSuccessful()) {
                    List<Document> items = task.getResult();
                    Log.d("app", String.format("successfully found %d documents", items.size()));
                    for (Document item : items) {
                        Log.d("app", String.format("successfully found:  %s", item.toString()));
                    }
                } else {
                    Log.e("app", "failed to find documents with: ", task.getException());
                }
            }
        });
    }


    /***************************************************************************
     * DELETE                                                                  *
     ***************************************************************************/
    public void deleteOne() {
        Document filterDoc = new Document().append("name", "legos");

        final Task<RemoteDeleteResult> deleteTask = itemsCollection.deleteOne(filterDoc);
        deleteTask.addOnCompleteListener(new OnCompleteListener<RemoteDeleteResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteDeleteResult> task) {
                if (task.isSuccessful()) {
                    long numDeleted = task.getResult().getDeletedCount();
                    Log.d("app", String.format("successfully deleted %d documents", numDeleted));
                } else {
                    Log.e("app", "failed to delete document with: ", task.getException());
                }
            }
        });

    }

    public void deleteMany() {
        Document filterDoc = new Document().append("reviews", new Document().append("$size", 0));

        final Task<RemoteDeleteResult> deleteTask = itemsCollection.deleteMany(filterDoc);
        deleteTask.addOnCompleteListener(new OnCompleteListener<RemoteDeleteResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteDeleteResult> task) {
                if (task.isSuccessful()) {
                    long numDeleted = task.getResult().getDeletedCount();
                    Log.d("app", String.format("successfully deleted %d documents", numDeleted));
                } else {
                    Log.e("app", "failed to delete document with: ", task.getException());
                }
            }
        });

    }


    /***************************************************************************
     * UPDATE                                                                  *
     ***************************************************************************/
    public void updateOneAddField() {
        Document filterDoc = new Document().append("name", "legos");
        Document updateDoc = new Document().append("$set",
                new Document()
                        .append("name", "blocks")
                        .append("price", 20.99)
                        .append("category", "toys")
        );

        final Task<RemoteUpdateResult> updateTask = itemsCollection.updateOne(filterDoc, updateDoc);
        updateTask.addOnCompleteListener(new OnCompleteListener<RemoteUpdateResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteUpdateResult> task) {
                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("app", String.format("successfully matched %d and modified %d documents", numMatched, numModified));
                } else {
                    Log.e("app", "failed to update document with: ", task.getException());
                }
            }
        });

    }

    public void updateOnePush() {
        Document filterDoc = new Document().append("name", "football");
        Document updateDoc = new Document().append("$push",
                new Document().append("reviews", new Document()
                        .append("username", "stitchfan2018")
                        .append("comment", "what a neat product")
                )
        );

        final Task<RemoteUpdateResult> updateTask = itemsCollection.updateOne(filterDoc, updateDoc);
        updateTask.addOnCompleteListener(new OnCompleteListener<RemoteUpdateResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteUpdateResult> task) {
                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("app", String.format("successfully matched %d and modified %d documents", numMatched, numModified));
                } else {
                    Log.e("app", "failed to update document with: ", task.getException());
                }
            }
        });

    }

    public void updateOneUpsert() {
        Document filterDoc = new Document().append("name", "board game");
        Document updateDoc = new Document().append("$inc", new Document().append("quantity", 5));
        RemoteUpdateOptions options = new RemoteUpdateOptions().upsert(true);


        final Task<RemoteUpdateResult> updateTask = itemsCollection.updateOne(filterDoc, updateDoc, options);
        updateTask.addOnCompleteListener(new OnCompleteListener<RemoteUpdateResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteUpdateResult> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getUpsertedId() != null) {
                        String upsertedId = task.getResult().getUpsertedId().toString();
                        Log.d("app", String.format("successfully upserted document with id: %s", upsertedId));
                    } else {
                        long numMatched = task.getResult().getMatchedCount();
                        long numModified = task.getResult().getModifiedCount();
                        Log.d("app", String.format("successfully matched %d and modified %d documents", numMatched, numModified));
                    }
                } else {
                    Log.e("app", "failed to update document with: ", task.getException());
                }
            }
        });
    }

    public void updateMany() {
        Document filterDoc = new Document();
        Document updateDoc = new Document().append("$mul", new Document().append("quantity", 10));

        final Task<RemoteUpdateResult> updateTask = itemsCollection.updateMany(filterDoc, updateDoc);
        updateTask.addOnCompleteListener(new OnCompleteListener<RemoteUpdateResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteUpdateResult> task) {
                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("app", String.format("successfully matched %d and modified %d documents", numMatched, numModified));
                } else {
                    Log.e("app", "failed to update document with: ", task.getException());
                }
            }
        });
    }

    /***************************************************************************
     * AGGREGATE                                                               *
     ***************************************************************************/
    public void aggregate() {
        List<Document> aggregationPipeLine = Arrays.asList(
                new Document().append("$group", new Document()
                        .append("_id", "$category")
                        .append("totalQuantity", new Document().append("$sum", "$quantity"))
                        .append("count", new Document().append("$sum", 1))
                )
        );

        itemsCollection.aggregate(aggregationPipeLine).forEach(item -> {
            Log.d("app", String.format("aggregation result:  %s", item.toString()));
        });

        // Another way to iterate through
        Task<List<Document>> itemsTask = itemsCollection.aggregate(aggregationPipeLine)
                .into(new ArrayList<Document>());
        itemsTask.addOnCompleteListener(new OnCompleteListener<List<Document>>() {
            @Override
            public void onComplete(@NonNull Task<List<Document>> task) {
                if (task.isSuccessful()) {
                    List<Document> items = task.getResult();
                    Log.d("app", String.format("%d aggregation results", items.size()));
                    for (Document item : items) {
                        Log.d("app", String.format("aggregation result:  %s", item.toString()));
                    }
                } else {
                    Log.e("app", "failed to perform aggregation with: ", task.getException());
                }
            }
        });
    }


    /***************************************************************************
     * AUTHENTICATION                                                          *
     ***************************************************************************/
    public void anonymousLogin() {
        // Get the default AppClient
        StitchAppClient stitchClient = Stitch.getDefaultAppClient();

        // Login with Anonymous credentials and handle the result
        stitchClient.getAuth().loginWithCredential(new AnonymousCredential()).addOnCompleteListener(new OnCompleteListener<StitchUser>() {
            @Override
            public void onComplete(@NonNull final Task<StitchUser> task) {
                if (task.isSuccessful()) {
                    Log.d("myApp", String.format(
                            "logged in as user %s with provider %s",
                            task.getResult().getId(),
                            task.getResult().getLoggedInProviderType()));
                } else {
                    Log.e("myApp", "failed to log in", task.getException());
                }
            }
        });
    }

    /***************************************************************************
     * EXTERNAL SERVICES                                                       *
     ***************************************************************************/
    public void sendTwilioSMS() {
        // Get the default AppClient
        StitchAppClient stitchClient = Stitch.getDefaultAppClient();

        // myTwilioService is the name of the Twilio service you created in the stitch UI,
        // and it is configured with a rule that allows the SendMessage action
        TwilioServiceClient twilio = stitchClient.getServiceClient(TwilioServiceClient.factory, "twilio");

        // Compose the SMS Message
        String sourceNumber = "+14758975236";
        String destNumber = "19145829330";
        String message = "This message is sent on Twilio from Android SDK";

        twilio.sendMessage(destNumber, sourceNumber, message);
    }

    public void sendSESEmail() {
        // Get the default AppClient
        StitchAppClient stitchClient = Stitch.getDefaultAppClient();

        // myAWSService is the name of the aws service you created in the stitch UI,
        // and it is configured with a rule that allows the SendEmail action on the SES API
        AwsServiceClient aws = stitchClient.getServiceClient(AwsServiceClient.factory, "myAWSService");

        // Compose the Email Message
        String sourceEmail = "tkaye407@gmail.com";
        String destEmail = "tyler.kaye@10gen.com";
        String message = "This message is sent on AWS SES from Android";
        String subject = "Pretty cool, right";
        Document args = new Document()
                .append("Destination", new Document()
                        .append("ToAddresses", Arrays.asList(destEmail)))
                .append("Source", sourceEmail)
                .append("Message", new Document().
                                append("Body", new Document()
                                        .append("Html", new Document()
                                                .append("Charset", "UTF-8")
                                                .append("Data", message)
                                        )
                                ).append("Subject", new Document().
                                append("Charset", "UTF-8").
                                append("Data", subject)
                        )
                );

        // Build the request object for SendEmail function in SES service
        AwsRequest.Builder request = new AwsRequest.Builder()
                .withService("ses")
                .withAction("SendEmail")
                .withArguments(args);

        // Execute the function and handle the result
        aws.execute(request.build(), Document.class)
                .addOnCompleteListener(new OnCompleteListener<Document>() {
                    @Override
                    public void onComplete(@NonNull Task<Document> task) {
                        if (task.isSuccessful()) {
                            final Document result = task.getResult();
                            Log.d("myApp", String.format("successfully send message with id %s", result.getString("MessageId")));
                        } else {
                            Log.e("myApp", "failed to log in", task.getException());
                        }
                    }
                });
    }
}
