SELECT "a"."password" AS "password" FROM "employee" AS "e" JOIN "auth" "a" ON "a"."id" = "e"."auth" WHERE "e"."id" = ?;