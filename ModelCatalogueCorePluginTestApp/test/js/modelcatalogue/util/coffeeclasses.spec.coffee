xdescribe "coffee classes playground", ->

  class Tester
    # this is private, only visible inside initializer
    classWithEquals     = "class with equals"

    # this is public on Tester function
    @classWithEqualsAt  = "class with equals and at sign"

    # this is also public on Tester function
    @classWithAtSign    : "class with at sign"

    # this as add to the prototype object of Tester, available on all Tester object
    classWithoutAtSign  : "class without at sign"


    # this is actually the Tester function returneed
    constructor         : (bar) ->
      # this is added to this object
      @instanceWithAtSign   = "instance with at sign #bar"

      # this is private, only visible inside Tester function (called constructor here)
      instanceWithoutAtSign = "instance without at sign #bar"

      # you need to use return explicitly if you want return something
      # from constructor function directly
      return "INSTANCE"



  # this is added to Tester object prototype, available to all Tester objects
  Tester::addedToPrototype = "added to prototype"


  it "static access", ->
    expect(Tester).toBeFunction()

    expect(Tester.classWithAtSign).toBeDefined()
    expect(Tester.classWithEqualsAt).toBeDefined()

    expect(Tester.classWithoutAtSign).toBeUndefined()
    expect(Tester.classWithEquals).toBeUndefined()
    expect(Tester.addedToPrototype).toBeUndefined()

    expect(Tester.toString()).toContain('function Tester(bar)')

  it "instance access", ->
    tester = new Tester("test")

    expect(tester).toBeObject()

    expect(tester.instanceWithAtSign).toBeDefined()
    expect(tester.classWithoutAtSign).toBeDefined()
    expect(tester.addedToPrototype).toBeDefined()

    expect(tester.classWithAtSign).toBeUndefined()
    expect(tester.classWithEquals).toBeUndefined()
    expect(tester.classWithEqualsAt).toBeUndefined()
    expect(tester.instanceWithoutAtSign).toBeUndefined()

  it "calling the function directly", ->

    expect(Tester).toBeFunction()

    result = Tester()

    expect(result).toBe('INSTANCE')

