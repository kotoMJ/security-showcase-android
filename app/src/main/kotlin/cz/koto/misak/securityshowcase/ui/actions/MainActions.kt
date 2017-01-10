package cz.koto.misak.securityshowcase.ui.actions


class MainActionsModel(
        val isGoalAvailable: Boolean = false,
        val isTransferAvailable: Boolean = false,
        val isPaymentAvailable: Boolean = false,
        val isDepositAvailable: Boolean = false,
        val isLoanAvailable: Boolean = false,
        val transferAction: () -> Unit,
        val paymentAction: () -> Unit,
        val depositAction: () -> Unit,
        val newGoalAction: () -> Unit)

//fun isTransferAvailable(): Boolean
//fun isGoalAvailable(): Boolean
//fun isPaymentAvailable(): Boolean
//fun isDepositAvailable(): Boolean
//fun doTransfer()
//fun doPayment()
//fun doDeposit()
//fun newGoal()