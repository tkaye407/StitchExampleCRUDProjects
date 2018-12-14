
function deleteOne() {
	const filterDoc = {name: "legos"};

	itemsCollection.deleteOne(filterDoc).then(result => {
		console.log("Successfully deleted " + result.deletedCount + " item(s).");
	}, (error) => {
	    alert("Error Deleting Document: " + error);
	});
}

function deleteMany() {
	const filterDoc = {reviews: {$size: 0}};

	itemsCollection.deleteMany(filterDoc).then(result => {
		console.log("Successfully deleted " + result.deletedCount + " item(s).");
	}, (error) => {
	    alert("Error Deleting Documents: " + error);
	});
}