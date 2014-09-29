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

package com.tcl.lzhang1.mymusic.db;

import java.util.List;

import com.tcl.lzhang1.mymusic.model.BaseModel;

/**
 * this interface defined the method to access the database
 * 
 * @author leizhang
 */
public interface DBOperator {

    /**
     * insert a record
     * 
     * @param addModel model
     */
    public void add(BaseModel addModel);

    /**
     * delete one record
     * 
     * @param id the primary key
     */
    public void delete(Integer id);

    /**
     * update one record
     * 
     * @param updateModel the model which to update
     */
    public void update(BaseModel updateModel);

    /**
     * find one record and return Model
     * 
     * @param id
     * @return
     */
    public BaseModel find(Integer id);

    /**
     * find all record
     * 
     * @return
     */
    public List<? extends BaseModel> findAll();

    /**
     * get count
     * 
     * @return
     */
    public Integer getCount();

    /**
     * save models
     * 
     * @param models
     */
    public void saveAll(List<? extends BaseModel> models);

    /**
     * delete
     * 
     * @param string
     */
    public void delete(String string);

    /**
     * @param sql
     */
    public BaseModel find(String sql);

    /**
     * find all record
     * 
     * @return
     */
    public List<? extends BaseModel> findAll(String sql);

}
