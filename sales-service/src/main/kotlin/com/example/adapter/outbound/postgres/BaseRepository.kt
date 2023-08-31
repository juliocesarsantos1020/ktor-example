package com.example.adapter.outbound.postgres

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Join
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigInteger
import java.sql.ResultSet

open class BaseRepository {

    object SafeSqlLogger : SqlLogger {
        private val logger = KotlinLogging.logger { }
        override fun log(context: StatementContext, transaction: Transaction) {
            logger.info { context.sql(TransactionManager.current()) }
        }
    }

    fun <T> query(
        database: Database,
        table: Table,
        addParameters: (p: MutableList<Op<Boolean>>) -> Unit,
        limit: BigInteger? = null,
        offset: BigInteger? = null,
        logSQL: Boolean = false,
        sortBy: Pair<Column<*>, SortOrder>? = null,
        join: Join? = null,
        func: (query: Query) -> T,
    ): T {
        val parameters = mutableListOf<Op<Boolean>>()

        addParameters(parameters)

        return transaction(database) {
            if (logSQL) {
                addLogger(SafeSqlLogger)
            }

            if (join != null) {
                join.selectAll().run {
                    createQuery(parameters, limit, offset, sortBy, func)
                }
            } else {
                table.selectAll().run {
                    createQuery(parameters, limit, offset, sortBy, func)
                }
            }
        }
    }

    private fun <T> Query.createQuery(
        parameters: MutableList<Op<Boolean>>,
        limit: BigInteger?,
        offset: BigInteger?,
        sortBy: Pair<Column<*>, SortOrder>?,
        func: (query: Query) -> T,
    ): T {
        parameters.forEach { parameter -> andWhere { parameter } }

        limit?.let {
            limit(n = it.toInt(), offset = offset?.toLong() ?: 0)
        }

        sortBy?.let { orderBy(it.first, it.second) }

        return func(this)
    }

    fun count(
        database: Database,
        table: Table,
        join: Join? = null,
        addParameters: (p: MutableList<Op<Boolean>>) -> Unit,
        logSQL: Boolean = false,
    ) = query(
        database = database,
        table = table,
        join = join,
        addParameters = addParameters,
        logSQL = logSQL,
    ) { query -> query.count() }

    fun <T> search(
        database: Database,
        table: Table,
        addParameters: (p: MutableList<Op<Boolean>>) -> Unit,
        limit: BigInteger? = null,
        offset: BigInteger? = null,
        logSQL: Boolean = false,
        sortBy: Pair<Column<*>, SortOrder>? = null,
        join: Join? = null,
        func: (r: ResultRow) -> T,
    ) = query(
        database = database,
        table = table,
        addParameters = addParameters,
        limit = limit,
        offset = offset,
        logSQL = logSQL,
        join = join,
        sortBy = sortBy,
    ) { query ->
        query.map { func(it) }
    }

    fun <T> dbQuery(database: Database, block: () -> T): T =
        runBlocking { newSuspendedTransaction(Dispatchers.IO, db = database) { block() } }

    fun <T> dbQueryInsert(database: Database, block: () -> T): T =
        transaction(db = database) { block() }

    fun <T : Any> String.execAndMap(database: Database, transform: (ResultSet) -> T): List<T> {
        val result = arrayListOf<T>()
        val query = this
        dbQuery(database) {
            transaction {
                exec(query) { rs ->
                    while (rs.next()) {
                        result += transform(rs)
                    }
                }
            }
        }
        return result
    }
}
