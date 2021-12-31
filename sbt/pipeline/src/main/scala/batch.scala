import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.types.DataType


case class batch()

object batch {

  def processDelays(spark : SparkSession, args : Array[String]):  Unit = {

    import spark.implicits._

    val customersPath = args(0)
    val ordersPath = args(1)
    val outputPath = args(2)

    val customerDF = readDF(spark: SparkSession,customersPath : String)
    val orderDF = readDF(spark: SparkSession,ordersPath  : String)


    

    val utc4 = Array("RR", "AM", "AC", "RO", "MT", "MS")

    val utc3 = Array("AP", "PA", "MA", "CE",
      "RN", "PB", "PI", "PE",
      "TO", "AL", "BA", "GO",
      "DF", "MG", "ES", "SP",
      "RJ", "PR", "SC", "RS", "SE")



    val utc4states= utc4.toSet
    val utc3states= utc3.toSet

    val df6 = orderDF.join(customerDF,orderDF("customer_id") ===  customerDF("customer_id"),"right")
      .select(
        col("order_id"),
        customerDF.col("customer_unique_id"),
        col("order_purchase_timestamp").cast(TimestampType),
        col("order_delivered_customer_date").cast(TimestampType),
        col("customer_state"),
        col("customer_city")
      )

    val df7 = df6.withColumn("updated_delivery_date", when(col("customer_state").isInCollection(utc4states),col("order_delivered_customer_date") - expr("INTERVAL 1 HOURS"))
      .when(col("customer_city") === "fernando de noronha",col("order_delivered_customer_date") + expr("INTERVAL 1 HOURS"))
      .otherwise(col("order_delivered_customer_date"))
    )

    val df8 = df7.withColumn("delivery_delay_in_days", datediff(col("updated_delivery_date"), col("order_purchase_timestamp")))

    val all10daysDelays = df8.where(col("delivery_delay_in_days") > 10)

    val CustomersWithDelays = all10daysDelays.select(col("customer_unique_id"))
				.repartition(1)
   				.write
   				.mode("overwrite")
          .option("header", "true")
   				.csv(outputPath)

  }



  def readDF(spark : SparkSession,path: String ): DataFrame = {

    import spark.implicits._

    val df = spark.read.format("csv")
      .option("header", "true")
      .load(s"$path")
    return df
  }

  def run(f: SparkSession => Unit) = {

    

    val builder = SparkSession.builder.appName("Spark Batch")
    val spark = builder.getOrCreate()
    f(spark)
    spark.close
  }


  def main(args: Array[String]): Unit = {

    val path = "."

    println("starting")

    val inputCustomers = s"$path/data/olist_customers_dataset.csv"
    val inputOrders = s"$path/data/olist_orders_dataset.csv"
    val outputPath = s"$path/output/results"




    if (!(new java.io.File(s"$path/output").exists)) {
      new java.io.File(s"$path/output").mkdirs
      println("adding output directory")
    }



    run(processDelays (_,Array(inputCustomers : String,inputOrders : String,outputPath : String)))

    println("end of processing")

  }

}
