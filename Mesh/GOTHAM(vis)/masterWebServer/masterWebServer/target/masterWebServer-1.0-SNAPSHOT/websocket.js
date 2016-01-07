/*
 * Copyright 2015-2016 junhoya924
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */




var wsUri = "ws://" + document.location.host + document.location.pathname + "javaWebSocket";
var websocket = new WebSocket(wsUri);

var jsonString = "{\"nodes\":[{\"name\":\"0\",\"group\":2},{\"name\":\"1\",\"group\":1},{\"name\":\"2\",\"group\":1},{\"name\":\"3\",\"group\":1},{\"name\":\"4\",\"group\":1}],\"links\":[]}";

websocket.onerror = function (evt) {
    onError(evt)
};

function onError(evt) {
    writeToScreen('<span style="color: red;">ERROR: (webConnection)</span> ' + evt.data);
    writeToScreen(wsUri);
}

var output = document.getElementById("output");
websocket.onopen = function (evt) {
    onOpen(evt)
};
websocket.onmessage = function (evt) {
    onMessage(evt)
};


setInterval(function () {
    writeToScreen(
            "           \<div class=\"pace pace-active\">\n" +
            "            <div class=\"pace-progress\" data-progress=\"50\" data-progress-text=\"50%\" style=\"-webkit-transform: translate3d(50%, 0px, 0px); \n" +
            "                 -ms-transform: translate3d(50%, 0px, 0px); transform: translate3d(50%, 0px, 0px);\">\n" +
            "                <div class=\"pace-progress-inner\">\n" +
            "                </div>\n" +
            "            </div>\n" +
            "            <div class=\"pace-activity\">\n" +
            "            </div>\n" +
            "        </div>");
}, 30000);

setInterval(function () {

    window.location.reload();
}, 50000);

function writeToScreen(message) {
    output.innerHTML += message + "<br>";

}

function onOpen() {
    writeToScreen("Connected to " + wsUri);
    websocket.send("hihi");

}

function onMessage(evt) {
    //writeToScreen(evt.data + ": on Message");
    //websocket.send("hihi");
    jsonString = evt.data;
    writeToScreen(evt.data);

    if (jsonString == "node is not enough") {
        writeToScreen('<span style="color: red;">ERROR:</span> ' + evt.data + " (under 3 node)");
        return;
    }
    var width = 960,
            height = 500;

    var color = d3.scale.category20()
            .domain(["red", "green", "blue"])
            .range(["#FF0000", "#009933", "#3399FF"]);

    var force = d3.layout.force()
            .charge(-120)
            .linkDistance(200)
            .size([width, height]);

    var svg = d3.select("body").append("svg")
            .attr("width", width)
            .attr("height", height);

    var graph = JSON.parse(jsonString);

    force
            .nodes(graph.nodes)
            .links(graph.links)
            .start();

    var link = svg.selectAll(".link")
            .data(graph.links)
            .enter().append("line")
            .attr("class", "link")
            .style("stroke-width", function (d) {
                return Math.sqrt(d.weight);
            });

    var node = svg.selectAll(".node")
            .data(graph.nodes)
            .enter().append("g")
            .attr("class", "node")
            .call(force.drag);

    node.append("circle")
            .attr("r", "5")
            .style("fill", function (d) {
                if (d.group == 1)
                    return color("green");
                else if (d.group == 2)
                    return color("blue");
                else
                    return color("red");
            });

    node.append("text")
            .attr("dx", 12)
            .attr("dy", ".15em")
            .text(function (d) {
                return d.name
            });

    force.on("tick", function () {
        link.attr("x1", function (d) {
            return d.source.x;
        })
                .attr("y1", function (d) {
                    return d.source.y;
                })
                .attr("x2", function (d) {
                    return d.target.x;
                })
                .attr("y2", function (d) {
                    return d.target.y;
                });

        node.attr("transform", function (d) {
            return "translate(" + d.x + "," + d.y + ")";
        });
    });
}
