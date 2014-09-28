/**
 * Copyright 2014 ZhangLei
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.tcl.lzhang1.mymusic.ui;

import android.os.Bundle;

/**
 * This interface was created for initing the activity <br/>
 * the order of call as follows: <li>getPreActivityData() <li/> <li>initView
 * <li/> <li>initViewData<li/>
 * 
 * @author leizhang
 */
public interface AcitivityInit {
    /**
     * get the pre activity's data
     * 
     * @param bundle the object which contains data
     */
    public abstract void getPreActivityData(Bundle bundle);

    /**
     * init view
     */
    public abstract void initView();

    /**
     * bind initial data to view
     */
    public abstract void initViewData();
}
