describe "mc.core.dateEnhancer", ->

  beforeEach module 'mc.core.dateEnhancer'

  it "Converts dates ISO strings to real dates", inject (enhance) ->
    date = enhance '2014-07-30T08:08:46Z'

    expect(angular.isDate(date)).toBeTruthy()


