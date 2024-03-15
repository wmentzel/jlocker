package com.randomlychosenbytes.jlocker.model

import com.google.gson.annotations.Expose

class Walk(name: String) : Entity(name) {
    @Expose
    var modules: MutableList<Module> = mutableListOf()
}