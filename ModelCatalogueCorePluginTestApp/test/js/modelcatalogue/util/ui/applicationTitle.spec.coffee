describe "mc.util.ui.applicationTitle", ->
  beforeEach module 'mc.util.ui.applicationTitle'
  beforeEach module (applicationTitleProvider) ->
    applicationTitleProvider.defaultTitle = "Model Catalogue Demo"
    return

  it "service changes the site title", inject (applicationTitle) ->
    expect(applicationTitle()).toBe("Model Catalogue Demo")
    expect(angular.element('title').text()).toBe("Model Catalogue Demo")

    applicationTitle("Brand New Title")

    expect(applicationTitle()).toBe("Brand New Title")
    expect(angular.element('title').text()).toBe("Brand New Title")



