describe('mc.util.delayedQueueExecutor', function () {
    beforeEach(module('mc.util.delayedQueueExecutor'));

    it("should exist", inject((delayedQueueExecutor) =>
        expect(delayedQueueExecutor).toBeDefined()
    ));

});
