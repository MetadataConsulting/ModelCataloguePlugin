(function() {
  var ignore = false
  if (!ignore) {
    describe("mc.util.MessagingClient module", function() {

      var StompClient;

      beforeEach(angular.mock.module('mc.util.MessagingClient', function() {

      }));

      describe('MessagingClient', function() {
        var MessagingClient, $rootScope;

        beforeEach(angular.mock.inject(function(_MessagingClient_, _$rootScope_) {
          MessagingClient = _MessagingClient_;
          $rootScope = _$rootScope_;
          StompClient = MessagingClient._getStompClient()
        }));

        it("client is defined", function() {
          expect(MessagingClient).toBeDefined();
        });

        it("connect and disconnect", function() {
          var frame = undefined;

          MessagingClient.connect().then(function(_frame_){
            frame = _frame_

            expect(frame).toBeUndefined();

            $rootScope.$apply();

            expect(MessagingClient.isConnected()).toBeTruthy();
            expect(frame).toBeDefined();

            MessagingClient.disconnect();

            $rootScope.$apply();

            expect(MessagingClient.isConnected()).toBeFalsy();
          });



        });

        it("subscribe and unsubscribe", function() {
          var subscription = undefined, unsubscribed = false;

          MessagingClient.subscribe('/foo/bar', function(_subscription_){
            subscription = _subscription_;

            $rootScope.$apply();

            expect(subscription).toBeDefined();
            expect(subscription.destination).toBe('/foo/bar');
            expect(subscription.headers).toBeDefined();
            expect(subscription.headers.foo).toBe('bar');

            subscription.unsubscribe = function () {
              unsubscribed = true;
            };

            MessagingClient.unsubscribe('/foo/bar');

            $rootScope.$apply();

            expect(unsubscribed).toBeTruthy();
          }, {foo: 'bar'});

        });

        // it("send", function() {
        //   MessagingClient.send('/foo/bar', {}, {foo: 'bar'});
        //
        //   $rootScope.$apply();
        //
        //   expect(StompClient.getSendPayload()).toBeDefined();
        //   expect(StompClient.getSendPayload().destination).toBe('/foo/bar');
        //   expect(StompClient.getSendPayload().body).toBeDefined();
        //   expect(StompClient.getSendPayload().body.foo).toBe('bar');
        //
        // });

        // getSendPayload method doesn't exist as far as I can tell -- James


      });
    });
  }
})()

