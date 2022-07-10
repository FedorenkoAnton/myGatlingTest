package com.gatlingtest

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class ComputerDataBaseRampConcurrentUsers extends Simulation {

	val httpProtocol = http
		.baseUrl("https://computer-database.gatling.io")
		.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-GB,en-US;q=0.9,en;q=0.8")
		.upgradeInsecureRequestsHeader("1")
		.userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36")

	val scn = scenario("ComputerDataBase")
		.exec(http("Load page")
			.get("/computers"))
		.pause(5)
		.exec(http("Create new computer")
			.get("/computers/new"))
		.pause(5)
		.exec(http("Get back on the home page")
			.post("/computers")
			.formParam("name", "Laptop")
			.formParam("introduced", "2022-01-01")
			.formParam("discontinued", "2025-01-01")
			.formParam("company", "13"))
		.pause(5)
		.exec(http("Search for the laptop")
			.get("/computers?f=Laptop"))

	setUp(
		scn.inject(
			incrementConcurrentUsers(5)
				.times(5)
				.eachLevelLasting(10)
				.separatedByRampsLasting(10)
				.startingFrom(10) // Int
		)
	).protocols(httpProtocol)
}