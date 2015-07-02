package com.rhodes.bsdiffdemo.component.bsdiff;

import com.rhodes.bsdiffdemo.utils.Logger;

/**
 * Created by xiet on 2015/6/30.
 */
public class Bsdiffer {
    static {
        Logger.log("Bsdiffer-System.loadLibrary");
        System.loadLibrary("diffpatch");
    }
    public static native int diff(String oldPkg,String newPkg,String patch);
    public static native int patch(String oldPkg,String newPkg,String patch);
}
