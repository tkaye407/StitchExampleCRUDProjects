
function deleteOne() {
	const filterDoc = {name: "legos"};

	itemsCollection.deleteOne(filterDoc).then(result => {
		console.log("Successfully deleted " + result.deletedCount + " item(s).");
	}, (error) => {
	    alert("Error Deleting Document: " + error);
	});
}

function deleteMany() {
	const filterDoc = {};

	itemsCollection.deleteMany(filterDoc).then(result => {
		console.log("Successfully deleted " + result.deletedCount + " item(s).");
	}).catch(error => {
	    alert("Error Deleting Documentss (this is being caught by promise rejection) " + error);
	});
}