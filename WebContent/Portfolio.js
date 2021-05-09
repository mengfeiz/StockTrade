var userName;
var socket;
function trade(i,ticker){
	var currentHour = new Date().getHours();
 	var currentMin = new Date().getMinutes();
	var currentDay = new Date().getDay();
 	var isOpen = ((currentHour>6 && currentHour<13) || (currentHour === 6 && currentMin>=30)) && currentDay >=1 && currentDay <=5;
	console.log("isOpen is "+isOpen);
	if(!isOpen){
		alert("You can't trade when the market is closed");
	}else{
		var xhttp = new XMLHttpRequest();
		var quantity = document.getElementById("Quantity"+i).value;
		var temp = "rad"+i;
		var ele = document.getElementsByName(temp);
		var rad;
		for(i = 0; i < ele.length; i++) {
            if(ele[i].checked)
            rad = ele[i].value;
        }
		if(rad === undefined || quantity === ""){
			alert("Invalid Input!");
			return;
		}
		//document.myform.gender.value
		var param = "Trade123?Quantity="+quantity;
		param += "&action="+rad;//document.getElementById("rad").value;
		param += "&userName="+getCookie("Username");
		param += "&ticker="+ticker;
		console.log(param);
		xhttp.open("GET", param, true);
		xhttp.onreadystatechange = function() {
			if (this.readyState == 4 && this.status == 200) {
				var myObj = this.responseText;
				console.log(myObj);
				alert(myObj);
				window.location = "Portfolio.html";
			}
		}
		xhttp.send();
		return false;
	}
	
}
function connectToServer() {
	console.log(new Date());
	var innerHTML = "";
	socket = new WebSocket("ws://localhost:8080/mengfei_CSCI201L_Assignment4/Portfolio"); 
	socket.onopen = function(event) {
		document.getElementById("cardContainer").innerHTML = "Please wait for data to be loaded...<br />";
		userName=getCookie("Username");
		socket.send(userName);
	}
	socket.onmessage = function(event) {
		let convertedResults = JSON.parse(event.data);
		console.log(convertedResults);
		innerHTML += "<div class='subtitle'>Cash Balance: </div>";
		innerHTML += "<div id='cashBalance'>"+convertedResults.cashBalance.toFixed(2)+"</div><br>";
		innerHTML += "<div class='subtitle'>Total Account Value: </div>";
		innerHTML += "<div id='totalValue'>"+convertedResults.totalAccountValue.toFixed(2)+"</div><br>";
		for(var i = 0; i < convertedResults.profileTickers.length; i++){
			innerHTML += "<br><div id='tickerContainer'>";
			innerHTML += "<div id='upper'><div id='ticker'>"+convertedResults.profileTickers[i].ticker+"   </div>";
			innerHTML += "<div id='name'>"+convertedResults.profileTickers[i].name+"</div></div>";
			innerHTML += "<div id='middle'><div id='midLeft'><div id='ll'><div class='text'>Quantity:</div><br><div class='text'>Avg. Cost / Share:</div><br><div class='text'>Total Cost:</div></div>";
			innerHTML += "<div id='lr'><div class='text'>"+convertedResults.profileTickers[i].quantity.toFixed(2)+"</div><br><div class='text'>"+convertedResults.profileTickers[i].avgCost.toFixed(2)+"</div><br><div class='text'>"+convertedResults.profileTickers[i].totalCost.toFixed(2)+"</div></div>";
			innerHTML += "</div><div id='midRight'>";
			innerHTML += "<div id='rl'><div class='text'>Change: </div><br><div class='text'>Current Price: </div><br><div class='text'>Market Value: </div></div>";
			innerHTML += "<div id='rr'>";
			var change = convertedResults.profileTickers[i].change.toFixed(2);
			if(change > 0){
				innerHTML += "<div id='upChange'>"+"<i id='arrowUp' class='fa fa-caret-up' aria-hidden='true'></i>   "+change+"</div>";
			}else if(change === 0){
				innerHTML += "<div class='midChange'>"+change+"</div>";
			}else{
				innerHTML += "<div id='downChange'>"+"<i id='arrowDown' class='fa fa-caret-down' aria-hidden='true'></i>   "+change+"</div>";		
			}//arrowpart done
			innerHTML += "<br><div class='text'>"+convertedResults.profileTickers[i].currentPrice.toFixed(2)+"</div>";
			innerHTML += "<br><div class='text'>"+convertedResults.profileTickers[i].marketValue.toFixed(2)+"</div>";
			innerHTML += "</div></div></div>";
			innerHTML += "<div id='bottom'><form name = 'myForm' method='get' onSubmit='return trade("+i+",\""+convertedResults.profileTickers[i].ticker+"\")'>";
			//innerHTML += "<div id='bottom'><form name = 'myForm' method='get' onSubmit='return trade(\""+convertedResults.profileTickers[i].ticker+"\","+quantity+","+rad+")";
			innerHTML += "<label for='Quantity"+i+"'>Quantity:</label>";
			innerHTML += "<input type='number' id='Quantity"+i+"' name='Quantity"+i+"'/><br>";
			
			innerHTML += "<label for = rad"+i+"></label><br>";
			innerHTML += "<input type='radio' name='rad"+i+"' id='buy' value='Buy'/>";
			innerHTML += "<label for='buy'>BUY       </label>";
			innerHTML += "<input type='radio' name='rad"+i+"' id='sale' value='Sale'/>";
			innerHTML += "<label for='sale'>SELL</label><br>";
			//<label for = gender>Gender:</label><br>
			//<input type="radio" id="male" name="gender" value="male">
			//<label for="male">Male</label><br>
			//<input type="radio" id="female" name="gender" value="female" checked>
			//<label for="female">Female</label><br>

			//innerHTML += "<input type='radio' name='rad"+i+"' id='rad"+i+"' value='Buy'/>";
			//innerHTML += "<label for='rad"+i+"'>BUY       </label>";
			//innerHTML += "<input type='radio' name='rad"+i+"' id='rad"+i+"' value='Sale'/><label for='rad"+i+"'>SELL</label><br>";
			//var quantity = document.getElementById("Quantity"+i).value;
			//var rad = document.getElementById("rad"+i).value;
			innerHTML += "<button type='submit' name='submit'>Submit</button>";
			innerHTML += "</form></div>";
			innerHTML += "</div>";
			innerHTML += "</div>";
			innerHTML += "</div>";
		}
		
		document.getElementById("cardContainer").innerHTML = innerHTML;
	}
	socket.onclose = function(event) {
		document.getElementById("cardContainer").innerHTML += "Disconnected!"; 
	}
}
