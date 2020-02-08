# graphql-schema-stitching-using-braid

This service stitches the schema from two different services using graphql braid.

* Service1 : https://github.com/imadi/graphql-service-one

* Service2 : https://github.com/imadi/graphql-service-two

I have provided 2 approaches (Approach 1 is enabled by default)

* **Approach 1** (uses a mix of yaml and java code)

We provide only schema stitching configuration in yaml file and schema is retrieved from graphql url

* **Approach 2** (uses complete yaml)

Entire schema configuration along with stitching information is mentioned in yaml file


