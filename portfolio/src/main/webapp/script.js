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
            commentListEle.appendChild(createThreadElement(comments[i]));
        }

    });
}

 function addComment () {
    var comment = document.getElementById("text-input").value;
    var img = document.getElementById("fileInput").files[0];
    console.log(img);
    var url = "/messages?text-input=".concat(comment);

    var fData  = new FormData();
    fData.append('text', comment);
    fData.append('image', img);
    console.log(fData);
    fetch('/blobstore-upload-url')
      .then((response) => {
        return response.text();
      })
      .then((imageUploadUrl) => {
          fetch(imageUploadUrl, {
            method: 'POST',
            body: fData
            })
            .then(response => getComments());
      });

}

 function deleteEntries () {
    var url = "/delete-data";
    fetch(url, {method: 'POST'}).then(response => {
        console.log(response);
        getComments();
    });
    // document.getElementById('comments-container').innerHTML = '';

}

function createThreadElement(commentEntity) {
  var commentJson = JSON.parse(commentEntity.propertyMap.value);
  var imgUrl = commentEntity.propertyMap.imageUrl;


  var text = commentJson.text;

  //Likes
  
  //Create Thread Wrapper
  var ThreadWrapper = document.createElement('div');
  ThreadWrapper.className = "thread";

  //Create Original Comment wrapper
  var origComment = document.createElement('div');
  origComment.value = ThreadWrapper;
  origComment.className = "origComment";
  var textOrigComment = document.createElement('p');
  var pic = document.createElement('img');

  //image
  if (imgUrl != undefined) {
    pic.src = imgUrl;
    pic.className = "threadPic";
    ThreadWrapper.appendChild(pic);
  }

  ThreadWrapper.appendChild(deleteButton(commentEntity));
  ThreadWrapper.appendChild(origComment);


  origComment.appendChild(textOrigComment);
  
  textOrigComment.innerText = text;

  //Create Reply Section
  var replySection = document.createElement('div');
  ThreadWrapper.appendChild(replySection);
  replySection.className = "replyWrapper";
  replyhtml(replySection, commentEntity.key, commentJson.replies, "");
  
  origComment.onclick = function () {reply(replySection, commentEntity.key, "")};


  //like section
  ThreadWrapper.appendChild(like(commentEntity));
  return ThreadWrapper;
}

function replyhtml(parentElem, key, replies, path) {
    var i = 0;
    //vertical line
    var vertLine = document.createElement('div');
    vertLine.className = "vert-line";
    parentElem.appendChild(vertLine);

    for(; i < replies.length; i++) {
        //reply
        var replyEntry = document.createElement('div');
        parentElem.appendChild(replyEntry);
        replyEntry.className = "reply-entry";
        var replyText = document.createElement('p');
        replyEntry.appendChild(replyText);
        replyText.innerText = replies[i].text;

        //replies to reply
        const replySection = document.createElement('div');
        parentElem.appendChild(replySection);
        replySection.className = "replyWrapper";

        //button to add new reply
        const newPath = path + i.toString();


        replyText.onclick = function () {
            reply(replySection, key, newPath);
        }

        replyhtml(replySection, key, replies[i].replies, newPath);

    }
}

function like(commentEntity) {
  const likeWrapper = document.createElement('div');
  const thumbsWrapper = document.createElement('div');
  const thumbsUp = document.createElement('i');
  thumbsUp.className = "fas fa-thumbs-up";
  thumbsWrapper.className = "commentButton";
  thumbsWrapper.onclick = function () {
      $.post("/like", commentEntity.key)
      .then(response => getComments())
  };
  thumbsWrapper.appendChild(thumbsUp);
  likeWrapper.appendChild(thumbsWrapper);
  const numLikes = document.createElement('div');
  numLikes.innerText = commentEntity.propertyMap.like;
  likeWrapper.appendChild(numLikes);
  return likeWrapper; 
}


function deleteButton(commentEntity) {
  const trashWrapper = document.createElement('div');
  trashWrapper.className = "commentButton";
  const trashIcon = document.createElement('i');
  trashIcon.className = "fa fa-trash";
  trashIcon.setAttribute('aria-hidden', 'true');
  trashWrapper.onclick = function () {

      $.post("/delete-data", commentEntity.key)
      .then(response => getComments())

  };
  trashWrapper.appendChild(trashIcon);
  return trashWrapper; 
}


function reply(ParentEle, key, path) {
    //delete other reply div areas
    var replyForms = document.getElementsByClassName("reply");
    if (replyForms != undefined) {
        var i = 0;
        for (; i < replyForms.length; i++) {
            replyForms[i].parentElement.removeChild(replyForms[i]);
        }
    }
    //create reply div
    var replydiv = document.createElement('div');
    replydiv.className = "reply";
    var textArea = document.createElement('textarea');
    textArea.innerText = "reply";
    var submitReply = document.createElement('button');
    submitReply.innerHTML = "submit Reply";


    submitReply.onclick = function () {
        key["reply-text"] = textArea.value;
        key["path"] = path;
        console.log(key);
        $.post("/reply", key)
            .then(response => getComments());

    };

    replydiv.appendChild(submitReply);
    replydiv.appendChild(textArea);
    ParentEle.appendChild(replydiv);
}
