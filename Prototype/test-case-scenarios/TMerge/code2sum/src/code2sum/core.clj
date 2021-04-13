(ns code2sum.core 
  (:require [javaparser-wrapper.core :as j]
            [summarizer.core :as s]
            [summarizer.summarizer :refer [summarize]]
            [ast-extractor.extractable :refer [extract-ast-data]])
  (:import (com.github.javaparser.ast.body MethodDeclaration))
)

;;functions copied from test cases to convert string to javaparser

(defn wrapStatement
  ([statement package] (format "
                            package %s;
                            public class Wrappa{
                                  public void somefunc() {
                                    %s
                                  }
                             }
      " package statement))

  ([statement] (format "
                                public class Wrappa{
                                      public void someFunc() {
                                        %s
                                      }
                                 }
          " statement)))


(defn parse-statement
  ([statement package]
   (j/parse-str (wrapStatement statement package)))
  ([statement]
   (j/parse-str (wrapStatement statement))))

(defn construct-ast-and-get-nodes [nodeType & statementAndPackage]
  (let [cu (apply parse-statement statementAndPackage)]
    (j/find-all nodeType cu)))

(defn construct-ast-and-get-node [& params]
  (first (apply construct-ast-and-get-nodes params)))

;; wrap a function in a dummy class
(defn wrapFunction 
    ([function] (format "
      public class Wrappa{
              %s
       }
  " function)))

;; sumarize from function string
(defn summarizeMethod [string]
  (def cu (j/parse-str (wrapFunction string)))
  (def node (first (j/find-all MethodDeclaration cu)))
  (def ast (extract-ast-data node))
  (def result (s/describe-test-method ast))
  (doseq [item result]
    (println "line")
    (println item)
    (println ""))
)

;; main function
(defn -main
  [& args]
  
  ;; (def method "public void noPattern() {\nint a = 0;\nassertEquals((0), a);\n}")

  ;; (summarizeMethod method)
  ;;(def method "Option x = new Option();\nOption y = new Option();\nx.setOption(\"hello\");\n")

  ;; (def method "URI uRI0 = MockURI.aFTPURI;
  ;;   KeycloakUriBuilder keycloakUriBuilder0 = KeycloakUriBuilder.fromUri(uRI0);
  ;;   String string0 = keycloakUriBuilder0.getHost();
  ;;   assertEquals((-1), keycloakUriBuilder0.getPort());
  ;;   assertNotNull(string0);")

  ;; (def method "URI primaryKeyUri = MockURI.aFTPURI;
  ;;   KeycloakUriBuilder uriBuilder = KeycloakUriBuilder.fromUri(primaryKeyUri);
  ;;   String host = uriBuilder.getHost();
  ;;   assertEquals((-1), uriBuilder.getPort());
  ;;   assertNotNull(host);")

  (def method "
        final PathFilter pathFilter = new NameFileFilter(PATH_FIXTURE);
        try (final DirectoryStream<Path> stream = PathUtils.newDirectoryStream(PathUtils.current(), pathFilter)) {
            final Iterator<Path> iterator = stream.iterator();
            final Path path = iterator.next();
            assertEquals(PATH_FIXTURE, path.getFileName().toString());
            assertFalse(iterator.hasNext());
        }")

  (def jn (construct-ast-and-get-node MethodDeclaration method))

  (def ast (extract-ast-data jn))

  (def result (s/describe-test-method ast))


  (doseq [item result]
    (println "line")
    (println item)
    (println ""))
  )
