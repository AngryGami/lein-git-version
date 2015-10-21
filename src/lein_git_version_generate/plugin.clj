(ns lein-git-version-generate.plugin
  (:require [robert.hooke])
  (:use
   clojure.pprint
   [leiningen.git-version :only [git-version]]))

(defn wrap-with-hook [f & args] 
  (do 
     (apply git-version args)
     (apply f args)))

(defn hooks []
  (robert.hooke/add-hook #'leiningen.compile/compile #'wrap-with-hook))
