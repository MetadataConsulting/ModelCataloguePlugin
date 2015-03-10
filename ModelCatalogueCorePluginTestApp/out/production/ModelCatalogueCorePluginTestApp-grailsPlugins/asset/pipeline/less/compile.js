var compile = function(fileText, paths) {

    globalPaths = paths;

    var parser = new(less.Parser);

    var result;
    parser.parse(fileText, function (e, tree) {

        if (tree)
            result = tree.toCSS({ compress: true });

        if (e instanceof Object) {
            Packages.asset.pipeline.less.LessProcessor.print('There is '+e.type+' Error '+e.message+' on line '+e.line+' in column '+e.column);
            throw e;
        }

    });

    return result;
};
