spark-submit --class com.base.WordCount --jars share/lib/*.jar,scala-1.0.0.jar --master yarn --deploy-mode cluster  scala-1.0.0.jar
spark-submit --class com.base.WordCount --jars share/lib/*.jar,scala-1.0.0.jar --master yarn --deploy-mode cluster  scala-1.0.0.jar
spark-submit --class com.base.WordCount --jars $(echo share/lib/*.jar|tr ' ' ',') --master yarn --deploy-mode cluster  scala-1.0.0.jar


spark-submit --class com.base.WordCount --jars $(echo lib/*.jar|tr ' ' ',') --master yarn --deploy-mode cluster  scala-1.0.0.jar
