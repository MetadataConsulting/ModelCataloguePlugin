package org.modelcatalogue.core.gebUtils;

import org.openqa.selenium.JavascriptExecutor;

public enum ScrollDirection {

    UP {
        @Override
        public void scroll(JavascriptExecutor executor) {
            executor.executeScript("window.scrollBy(0,-250)", "");
        }
    },

    DOWN{
        @Override
        public void scroll(JavascriptExecutor executor) {
            executor.executeScript("window.scrollBy(0,250)", "");
        }
    };

    public abstract void scroll(JavascriptExecutor executor);
}
