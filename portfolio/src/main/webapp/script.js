// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
let i = 0;

$(document).ready(function(){
  // Add smooth scrolling to all links
  $("img").on()

  $("a").on('click', function(event) {

    // Make sure this.hash has a value before overriding default behavior
    if (this.hash !== "") {
      // Prevent default anchor click behavior
      event.preventDefault();

      // Store hash
      var hash = this.hash;

      // Using jQuery's animate() method to add smooth page scroll
      // The optional number (800) specifies the number of milliseconds it takes to scroll to the specified area
      $('html, body').animate({
        scrollTop: $(hash).offset().top
      }, 800, function(){
   
        // Add hash (#) to URL when done scrolling (default click behavior)
        window.location.hash = hash;
      });
    } 
  });
});


/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['while it is always best to believe in oneself, a little help from others can be a great blessing.',
       'pride is not the opposite of shame, but it’s source. True humility is the only antidote to shame.',
        'Life happens wherever you are, whether you make it or not.'];

  // cycle through
  i = (i + 1) % (greetings.length)
  const greeting = greetings[i];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

/**
 * Fetches a random quote from the server and adds it to the DOM.
 */
function getSmessage() {
  fetch('/data').then(response => response.json()).then((quote) => {
    document.getElementById('scon').innerText = quote;
  });
}

function getComments() {
    var querySize = document.getElementById("query-size").value;
    console.log(querySize);
    if (querySize == undefined) {
        querySize = 10;
    }
    console.log("get comments called");
    var url = '/messages?query-size='.concat(querySize.toString(10));
    fetch(url, {method: 'GET'}).then(response => response.json()).then((comments) => {
        console.log("received comments: ");
        console.log(comments);
        const commentListEle = document.getElementById('comments-container');
        commentListEle.innerHTML = '';
        var i;
        for(i = 0; i < comments.length; i++){
            commentListEle.appendChild(createListElement(comments[i]));
        }
    });
}

function addComment () {
    var comment = document.getElementById("text-input").value;
    var url = "/messages?text-input=".concat(comment);
    param = { 
        'text-input' : comment
        }
    console.log('adding comment:\n' + comment);
    fetch(url, {
        method: 'POST',
         headers: {
          'Content-Type': 'application/json'
        },
        })
    getComments();
}

function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}