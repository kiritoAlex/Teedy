'use strict';

/**
 * Chat controller.
 */
angular.module('docs').controller('Chat', ['$scope', '$stateParams', '$http', function($scope, $stateParams, $http) {
  $scope.username = $stateParams.username;
  $scope.messages = [];
  $scope.newMessage = '';
  $scope.currentUser = null;

  // 获取当前用户信息
  $http.get('../api/user').then(function(response) {
    $scope.currentUser = response.data.username;
  });

  // 格式化时间
  $scope.formatTime = function(timestamp) {
    var date = new Date(timestamp);
    return date.toLocaleTimeString();
  };

  // 判断消息是否是自己发送的
  $scope.isOwnMessage = function(message) {
    return message.sender_id === $scope.currentUser.id;
  };

  // Load conversation
  function loadConversation() {
      $http.get('../api/chat/conversation/' + $scope.username)
          .then(function(response) {
              $scope.messages = response.data.messages;
              scrollToBottom();
          });
  }

  // Send message
  $scope.sendMessage = function() {
      if (!$scope.newMessage.trim()) {
          return;
      }

      var data = $.param({
          content: $scope.newMessage
      });

      $http({
          method: 'POST',
          url: '../api/chat/send/' + $scope.username,
          data: data,
          headers: {
              'Content-Type': 'application/x-www-form-urlencoded'
          }
      }).then(function() {
          $scope.newMessage = '';
          loadConversation();
      });
  };

  // Scroll to bottom of chat
  function scrollToBottom() {
      var chatMessages = document.getElementById('chat-messages');
      if (chatMessages) {
          chatMessages.scrollTop = chatMessages.scrollHeight;
      }
  }

  // Load initial conversation
  loadConversation();

  // Poll for new messages every 5 seconds
  setInterval(loadConversation, 5000);
}]); 