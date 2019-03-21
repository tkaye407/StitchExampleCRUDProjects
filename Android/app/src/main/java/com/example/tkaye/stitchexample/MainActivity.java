package com.example.tkaye.stitchexample;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Button;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

// Stitch-Related Imports
import com.google.android.gms.tasks.Tasks;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
import com.mongodb.stitch.core.auth.providers.userpassword.UserPasswordCredential;
import com.mongodb.stitch.core.services.mongodb.remote.ChangeEvent;
import com.mongodb.stitch.core.services.mongodb.remote.ChangeStream;
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

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.types.ObjectId;


public class MainActivity extends AppCompatActivity {

    // Auth Buttons
    Button logoutButton, userpassLoginButton, anonLoginButton, removeAllUsersButton;

    // MongoButtons
    Button  insertOneButton, insertManyButton,
            findOneButton, findManyButton,
            updateOneButton, updateManyButton,
            deleteOneButton, deleteManyButton,
            watchButton, aggregateButton;

    // Other Services
    Button sesButton, twilioButton;

    private StitchAppClient stitchClient;
    private RemoteMongoClient mongoClient;
    private RemoteMongoCollection itemsCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Auth Buttons
        anonLoginButton      = (Button) findViewById(R.id.anonLoginButton);
        userpassLoginButton  = (Button) findViewById(R.id.userpassLoginButton);
        removeAllUsersButton = (Button) findViewById(R.id.removeAllUsersButton);
        logoutButton         = (Button) findViewById(R.id.logoutButton);

        // MongoButtons
        findOneButton    = (Button) findViewById(R.id.findOneButton);
        findManyButton   = (Button) findViewById(R.id.findManyButton);
        insertOneButton  = (Button) findViewById(R.id.insertOneButton);
        insertManyButton = (Button) findViewById(R.id.insertManyButton);
        updateOneButton  = (Button) findViewById(R.id.updateOneButton);
        updateManyButton = (Button) findViewById(R.id.updateManyButton);
        deleteOneButton  = (Button) findViewById(R.id.deleteOneButton);
        deleteManyButton = (Button) findViewById(R.id.deleteManyButton);
        watchButton      = (Button) findViewById(R.id.watchButton);
        aggregateButton  = (Button) findViewById(R.id.aggregateButton);

        // Other service buttons
        sesButton = (Button) findViewById(R.id.sesButton);
        twilioButton = (Button) findViewById(R.id.twilioButton);

        stitchClient = Stitch.getDefaultAppClient();
        mongoClient = stitchClient.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
        itemsCollection = mongoClient.getDatabase("store").getCollection("items");


        anonLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anonymousLogin();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });


        removeAllUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              removeAllUsers();
            }
        });

        userpassLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               userPassLogin();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newAlertWithMessageOnUIThread("logout", "success");
            }
        });

        // Mongo Actions
        findOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findOne();
            }
        });

        findManyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findMany();
            }
        });

        insertOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertOne();
            }
        });

        insertManyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertMany();
            }
        });

        deleteOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteOne();
            }
        });

        deleteManyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMany();
            }
        });

        updateOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // updateOneAddField();
                updateOnePush();
                // updateOneUpsert();
            }
        });

        updateManyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMany();
            }
        });

        watchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                watch();
            }
        });

        aggregateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aggregate();
            }
        });

        // Other Services:
        sesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSESEmail();
            }
        });

        twilioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTwilioSMS();
            }
        });
    }

    public void newAlertWithMessageOnUIThread(String funcName, String result) {
        Context context = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("app", String.format("Alert for func %s with result: %s", funcName, result));
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage(funcName + ": " + result);
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
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
                    newAlertWithMessageOnUIThread("insertOne", task.getResult().getInsertedId().toString());
                } else {
                    newAlertWithMessageOnUIThread("insertOne", String.format("failed with err: %s", task.getException().getLocalizedMessage()));
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
                    newAlertWithMessageOnUIThread("insertMany", String.format("successfully inserted %d items with ids: %s",
                            task.getResult().getInsertedIds().size(),
                            task.getResult().getInsertedIds().toString()));
                } else {
                    newAlertWithMessageOnUIThread("insertMany", String.format("failed with err: %s", task.getException().getLocalizedMessage()));
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
                        newAlertWithMessageOnUIThread("findOne", "Could not find any matching documents");
                    } else {
                        newAlertWithMessageOnUIThread("findOne", task.getResult().toJson());
                    }
                } else {
                    newAlertWithMessageOnUIThread("findOne", String.format("failed with err: %s", task.getException().getLocalizedMessage()));
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
                    String res = "";
                    List<Document> items = task.getResult();
                    res += String.format("successfully found %d documents [", items.size());
                    for (Document item : items) {
                        res += item.toString() + ", ";
                    }
                    res += "]";
                    newAlertWithMessageOnUIThread("findMany", res);
                } else {
                    newAlertWithMessageOnUIThread("findMany", String.format("failed with err: %s", task.getException().getLocalizedMessage()));
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
                    newAlertWithMessageOnUIThread("deleteOne", String.format("successfully deleted %d documents", task.getResult().getDeletedCount()));
                } else {
                    newAlertWithMessageOnUIThread("deleteOne", String.format("failed with err: %s", task.getException().getLocalizedMessage()));
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
                    newAlertWithMessageOnUIThread("deleteMany", String.format("successfully deleted %d documents", task.getResult().getDeletedCount()));
                } else {
                    newAlertWithMessageOnUIThread("deleteMany", String.format("failed with err: %s", task.getException().getLocalizedMessage()));
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
                    newAlertWithMessageOnUIThread("updateOneAddField", String.format("successfully matched %d and modified %d documents", numMatched, numModified));
                } else {
                    newAlertWithMessageOnUIThread("updateOneAddField", String.format("failed with err: %s", task.getException().getLocalizedMessage()));
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
                    newAlertWithMessageOnUIThread("updateOnePush", String.format("successfully matched %d and modified %d documents", numMatched, numModified));
                } else {
                    newAlertWithMessageOnUIThread("updateOnePush", String.format("failed with err: %s", task.getException().getLocalizedMessage()));
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
                        newAlertWithMessageOnUIThread("updateOneUpsert",  String.format("successfully upserted document with id: %s", upsertedId));
                    } else {
                        long numMatched = task.getResult().getMatchedCount();
                        long numModified = task.getResult().getModifiedCount();
                        newAlertWithMessageOnUIThread("updateOneUpsert", String.format("successfully matched %d and modified %d documents", numMatched, numModified));
                    }
                } else {
                    newAlertWithMessageOnUIThread("updateOneUpsert", String.format("failed with err: %s", task.getException().getLocalizedMessage()));
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
                    newAlertWithMessageOnUIThread("updateMany", String.format("successfully matched %d and modified %d documents", numMatched, numModified));
                } else {
                    newAlertWithMessageOnUIThread("updateMany", String.format("failed with err: %s", task.getException().getLocalizedMessage()));
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
                    String res = "";
                    List<Document> items = task.getResult();
                    res += String.format("successfully aggregated %d documents [", items.size());
                    for (Document item : items) {
                        res += item.toString() + ", ";
                    }
                    res += "]";
                    newAlertWithMessageOnUIThread("aggregate", res);
                } else {
                    newAlertWithMessageOnUIThread("aggregate", String.format("failed with err: %s", task.getException().getLocalizedMessage()));
                }
            }
        });
    }

    /***************************************************************************
     * WATCH                                                                   *
     ***************************************************************************/
    private void blockAndHandleChangeEvent(ChangeStream<Task<ChangeEvent>, Document> changeStream) {
        try {
            changeStream.nextEvent().addOnCompleteListener(new OnCompleteListener<ChangeEvent>() {
                @Override
                public void onComplete(@NonNull Task<ChangeEvent> task) {
                    if (task.isSuccessful()) {
                        ChangeEvent changeEvent = task.getResult();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                newAlertWithMessageOnUIThread("watch", changeEvent.getOperationType().name());
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                newAlertWithMessageOnUIThread("watch", String.format("failed change event with err: %s", task.getException().getLocalizedMessage()));

                            }
                        });
                    }
                    blockAndHandleChangeEvent(changeStream);
                }
            });
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    newAlertWithMessageOnUIThread("watch", String.format("failed try/catch with err: %s", e.getLocalizedMessage()));
                }
            });
            blockAndHandleChangeEvent(changeStream);
        }

    }

    public void watch(){
        List<ObjectId> ids = new ArrayList<>();
        RemoteFindIterable findResults = itemsCollection.find().projection(new Document().append("_id", 1));

        Task <List<Document>> itemsTask = findResults.into(new ArrayList<Document>());
        itemsTask.addOnCompleteListener(new OnCompleteListener <List<Document>> () {
            @Override
            public void onComplete(@NonNull Task<List<Document>> task) {
                if (task.isSuccessful()) {
                    List<Document> items = task.getResult();
                    for (Document item: items) {
                        ids.add(item.getObjectId("_id"));
                    }

                    ObjectId[] idsArr = new ObjectId[ids.size()];
                    idsArr = ids.toArray(idsArr);
                    Task<ChangeStream<Task<ChangeEvent>, Document>> cs = itemsCollection.watch(idsArr);
                    cs.addOnCompleteListener(new OnCompleteListener<ChangeStream<Task<ChangeEvent>, Document>>() {
                        @Override
                        public void onComplete(@NonNull Task<ChangeStream<Task<ChangeEvent>, Document>> task) {
                            if (task.isSuccessful()) {
                                ChangeStream<Task<ChangeEvent>, Document> changeStream = task.getResult();
                                newAlertWithMessageOnUIThread("watch", "Successfully set up change stream");
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        while(true) {
                                            try {
                                                ChangeEvent<Document> changeEvent = Tasks.await(changeStream.nextEvent());
                                                String changeEventStr = String.format("Change event of type %s with full doc %s", changeEvent.getOperationType(), changeEvent.getDocumentKey().toJson());
                                                newAlertWithMessageOnUIThread("watch", changeEventStr);
                                            } catch (ExecutionException e) {
                                                // The Task failed, this is the same exception you'd get in a non-blocking failure handler.
                                                newAlertWithMessageOnUIThread("watch", String.format("failed change event with ExecutionException: %s", e.getLocalizedMessage()));
                                            } catch (Exception e) {
                                                // Any other exception occurred while waiting for the task to complete.
                                                newAlertWithMessageOnUIThread("watch", String.format("failed change event with other Exception: %s", e.getLocalizedMessage()));
                                            }
                                        }
                                    }
                                }).start();
                            } else {
                                newAlertWithMessageOnUIThread("watch", String.format("failed initial watch() with err: %s", task.getException().getLocalizedMessage()));
                            }
                        }
                    });
                } else {
                    Log.e("app", "failed to find documents with: ", task.getException());
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
                    newAlertWithMessageOnUIThread("anonymousLogin", String.format("logged in as user %s with provider %s", task.getResult().getId(), task.getResult().getLoggedInProviderType()));
                } else {
                    newAlertWithMessageOnUIThread("anonymousLogin", String.format("failed with err: %s", task.getException().getLocalizedMessage()));
                }
            }
        });
    }

    public void logout() {
        // Get the default AppClient
        StitchAppClient stitchClient = Stitch.getDefaultAppClient();

        // Login with Anonymous credentials and handle the result
        stitchClient.getAuth().logout().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    newAlertWithMessageOnUIThread("logout", "success");
                } else {
                    newAlertWithMessageOnUIThread("logout", String.format("failed with err: %s", task.getException().getLocalizedMessage()));
                }
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

    public void userPassLogin() {
        // Get the default AppClient
        StitchAppClient stitchClient = Stitch.getDefaultAppClient();

        // Login with Anonymous credentials and handle the result
        stitchClient.getAuth().loginWithCredential(new UserPasswordCredential("tkaye407@gmail.com", "password")).addOnCompleteListener(new OnCompleteListener<StitchUser>() {
            @Override
            public void onComplete(@NonNull final Task<StitchUser> task) {
                if (task.isSuccessful()) {
                    newAlertWithMessageOnUIThread("logout", String.format("num users after: %d", stitchClient.getAuth().listUsers().size()));
                } else {
                    newAlertWithMessageOnUIThread("logout", String.format("failed with err: %s", task.getException().getLocalizedMessage()));
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
