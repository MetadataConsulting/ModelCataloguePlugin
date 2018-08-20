angular.module('mc.util.mdxFeatures', []).provider('mdxFeatures', [ ->

  mdxFeaturesProvider = {}

  ###::
  type MDXFeatures = {
    northThamesFeatures: boolean,
    gelFeatures: boolean
  }
  ###

  mdxFeaturesObj = {
    northThamesFeatures: false,
    gelFeatures: false
  }

  mdxFeatures =
    ###
    @return true if any feature in featureList is true in mdxFeatures
    ###
    availableByAnyFeature: (featureList) ->

      availableByFeatureFlag = false
      angular.forEach(featureList, (featureName) ->
        if (mdxFeaturesObj[featureName])
          availableByFeatureFlag = true
      )

      return availableByFeatureFlag


  mdxFeaturesProvider.setFeatures = (features) ->
    mdxFeaturesObj = features

  mdxFeaturesProvider.$get = [ ->
    return mdxFeatures
  ]

  return mdxFeaturesProvider
])
