package com.example.freelancecontrolmobile

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.freelancecontrolmobile.data.ClientDao
import com.example.freelancecontrolmobile.data.DatabaseHelper
import com.example.freelancecontrolmobile.data.ProjectDao
import com.example.freelancecontrolmobile.data.TimeEntryDao
import com.example.freelancecontrolmobile.model.Client
import com.example.freelancecontrolmobile.model.Project
import com.example.freelancecontrolmobile.model.TimeEntry
import com.example.freelancecontrolmobile.ui.theme.FreelanceControlMobileTheme
import com.example.freelancecontrolmobile.util.TimeCalculator

/**
 * Enum para gerenciar a navegação entre as telas.
 */
enum class AppScreen {
    HOME, CLIENTS, PROJECTS, TIME_ENTRIES, SUMMARY
}

class MainActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var clientDao: ClientDao
    private lateinit var projectDao: ProjectDao
    private lateinit var timeEntryDao: TimeEntryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicialização do banco e DAOs
        dbHelper = DatabaseHelper(this)
        clientDao = ClientDao(dbHelper)
        projectDao = ProjectDao(dbHelper)
        timeEntryDao = TimeEntryDao(dbHelper)

        enableEdgeToEdge()
        setContent {
            FreelanceControlMobileTheme {
                var currentScreen by remember { mutableStateOf(AppScreen.HOME) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        AppScreen.HOME -> HomeScreen(
                            onNavigate = { currentScreen = it }
                        )
                        AppScreen.CLIENTS -> ClientsScreen(
                            clientDao = clientDao,
                            onBack = { currentScreen = AppScreen.HOME }
                        )
                        AppScreen.PROJECTS -> ProjectsScreen(
                            clientDao = clientDao,
                            projectDao = projectDao,
                            onBack = { currentScreen = AppScreen.HOME }
                        )
                        AppScreen.TIME_ENTRIES -> TimeEntriesScreen(
                            projectDao = projectDao,
                            timeEntryDao = timeEntryDao,
                            onBack = { currentScreen = AppScreen.HOME }
                        )
                        AppScreen.SUMMARY -> SummaryScreen(
                            clientDao = clientDao,
                            projectDao = projectDao,
                            timeEntryDao = timeEntryDao,
                            onBack = { currentScreen = AppScreen.HOME }
                        )
                    }
                }
            }
        }
    }
}

// --- COMPONENTES REUTILIZÁVEIS ---

@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(12.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.width(8.dp))
        }
        Text(text = text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}

@Composable
fun StatusChip(status: String) {
    val (color, label) = when (status.lowercase()) {
        "ativo" -> Color(0xFF16A34A) to "Ativo"
        "pausado" -> Color(0xFFF59E0B) to "Pausado"
        "finalizado" -> Color(0xFF2563EB) to "Finalizado"
        else -> MaterialTheme.colorScheme.outline to status.replaceFirstChar { it.uppercase() }
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

// --- TELAS DO APLICATIVO ---

/**
 * HomeScreen: Menu moderno com cards e ícones básicos (Core).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: (AppScreen) -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Controle de Freelance", fontWeight = FontWeight.ExtraBold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Bem-vindo de volta!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Text(
                text = "O que deseja gerenciar hoje?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                textAlign = TextAlign.Start
            )

            MenuCard("Clientes", "Gerencie sua base de contatos", Icons.Default.Person, Color(0xFF3B82F6)) {
                onNavigate(AppScreen.CLIENTS)
            }
            // Substituído Icons.Default.Assignment por Icons.Default.List
            MenuCard("Projetos", "Controle seus contratos e taxas", Icons.Default.List, Color(0xFF2563EB)) {
                onNavigate(AppScreen.PROJECTS)
            }
            // Substituído Icons.Default.Schedule por Icons.Default.Refresh
            MenuCard("Registros de Horas", "Lance seu tempo trabalhado", Icons.Default.Refresh, Color(0xFF1D4ED8)) {
                onNavigate(AppScreen.TIME_ENTRIES)
            }
            // Substituído Icons.Default.BarChart por Icons.Default.Info
            MenuCard("Resumo Financeiro", "Veja seu desempenho geral", Icons.Default.Info, Color(0xFF1E40AF)) {
                onNavigate(AppScreen.SUMMARY)
            }
        }
    }
}

@Composable
fun MenuCard(title: String, subTitle: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = subTitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.weight(1f))
            // Substituído Icons.Default.ChevronRight por Icons.Default.KeyboardArrowRight
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}

/**
 * ClientsScreen: Cadastro limpo e lista em cards.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsScreen(clientDao: ClientDao, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var clients by remember { mutableStateOf(emptyList<Client>()) }

    fun refresh() { clients = clientDao.getAllClients() }
    LaunchedEffect(Unit) { refresh() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clientes", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Voltar") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionTitle("Novo Cliente")
                    OutlinedTextField(
                        value = name, onValueChange = { name = it }, label = { Text("Nome Completo") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = email, onValueChange = { email = it }, label = { Text("E-mail") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = phone, onValueChange = { phone = it }, label = { Text("Telefone") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                    )
                    PrimaryButton(
                        text = "Salvar Cliente",
                        // Substituído Icons.Default.Save por Icons.Default.Check
                        icon = Icons.Default.Check,
                        onClick = {
                            if (name.isNotBlank()) {
                                clientDao.insertClient(Client(name = name, email = email, phone = phone))
                                name = ""; email = ""; phone = ""; refresh()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            SectionTitle("Lista de Clientes")

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                items(clients) { client ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(text = client.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                if (!client.email.isNullOrBlank()) Text(text = client.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (!client.phone.isNullOrBlank()) Text(text = client.phone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { client.id?.let { clientDao.deleteClient(it); refresh() } }) {
                                Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * ProjectsScreen: UX aprimorada com chips de status e dropdowns.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(clientDao: ClientDao, projectDao: ProjectDao, onBack: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var hourlyRate by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("ativo") }
    
    var clients by remember { mutableStateOf(emptyList<Client>()) }
    var projects by remember { mutableStateOf(emptyList<Project>()) }
    var selectedClient by remember { mutableStateOf<Client?>(null) }
    var clientExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var projectToDelete by remember { mutableStateOf<Project?>(null) }

    fun refresh() { clients = clientDao.getAllClients(); projects = projectDao.getAllProjects() }
    LaunchedEffect(Unit) { refresh() }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Excluir Projeto", fontWeight = FontWeight.Bold) },
            text = { Text("Deseja mesmo remover o projeto \"${projectToDelete?.title}\"? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(onClick = {
                    projectToDelete?.id?.let { projectDao.deleteProject(it); refresh() }
                    showDeleteDialog = false
                }) { Text("Excluir", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Projetos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Voltar") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            if (clients.isEmpty()) {
                Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Text("Cadastre um cliente antes de criar projetos.", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.error)
                }
            } else {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SectionTitle("Novo Projeto")
                        
                        Box(Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = selectedClient?.name ?: "Selecione um cliente",
                                onValueChange = {}, readOnly = true, label = { Text("Cliente") },
                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                            )
                            Box(Modifier.matchParentSize().clickable { clientExpanded = true })
                            DropdownMenu(expanded = clientExpanded, onDismissRequest = { clientExpanded = false }) {
                                clients.forEach { client ->
                                    DropdownMenuItem(text = { Text(client.name) }, onClick = { selectedClient = client; clientExpanded = false })
                                }
                            }
                        }

                        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Nome do Projeto") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                        OutlinedTextField(value = hourlyRate, onValueChange = { hourlyRate = it }, label = { Text("Valor p/ Hora (R$)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                        
                        Box(Modifier.fillMaxWidth()) {
                            val statusLabels = mapOf("ativo" to "Ativo", "pausado" to "Pausado", "finalizado" to "Finalizado")
                            OutlinedTextField(
                                value = statusLabels[status] ?: "Ativo",
                                onValueChange = {}, readOnly = true, label = { Text("Status") },
                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                            )
                            Box(Modifier.matchParentSize().clickable { statusExpanded = true })
                            DropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                                statusLabels.forEach { (key, label) ->
                                    DropdownMenuItem(text = { Text(label) }, onClick = { status = key; statusExpanded = false })
                                }
                            }
                        }

                        // Substituído Icons.Default.Save por Icons.Default.Check
                        PrimaryButton(text = "Salvar Projeto", icon = Icons.Default.Check, modifier = Modifier.fillMaxWidth(), onClick = {
                            val rate = hourlyRate.toDoubleOrNull()
                            if (selectedClient != null && title.isNotBlank() && rate != null) {
                                projectDao.insertProject(Project(clientId = selectedClient!!.id!!, title = title, description = description, hourlyRate = rate, status = status))
                                title = ""; hourlyRate = ""; selectedClient = null; refresh()
                            }
                        })
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            SectionTitle("Projetos em Andamento")

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                items(projects) { project ->
                    val clientName = clients.find { it.id == project.clientId }?.name ?: "..."
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = project.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                    Spacer(Modifier.width(8.dp))
                                    StatusChip(project.status)
                                }
                                Text(text = "Cliente: $clientName", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "Taxa: R$ ${project.hourlyRate}/h", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { projectToDelete = project; showDeleteDialog = true }) {
                                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * TimeEntriesScreen: Foco total na clareza dos cálculos e organização.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeEntriesScreen(projectDao: ProjectDao, timeEntryDao: TimeEntryDao, onBack: () -> Unit) {
    var selectedProject by remember { mutableStateOf<Project?>(null) }
    var workDate by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    var projects by remember { mutableStateOf(emptyList<Project>()) }
    var entries by remember { mutableStateOf(emptyList<TimeEntry>()) }
    var projectExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var entryToDelete by remember { mutableStateOf<TimeEntry?>(null) }

    fun refresh() { projects = projectDao.getAllProjects(); entries = timeEntryDao.getAllTimeEntries() }
    LaunchedEffect(Unit) { refresh() }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Remover Registro") },
            text = { Text("Deseja apagar este registro de horas?") },
            confirmButton = {
                TextButton(onClick = {
                    entryToDelete?.id?.let { timeEntryDao.deleteTimeEntry(it); refresh() }
                    showDeleteDialog = false
                }) { Text("Remover", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Horas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionTitle("Novo Lançamento")
                    
                    Box {
                        OutlinedTextField(
                            value = selectedProject?.title ?: "Escolha o projeto",
                            onValueChange = {}, readOnly = true, label = { Text("Projeto") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                        )
                        Box(Modifier.matchParentSize().clickable { projectExpanded = true })
                        DropdownMenu(expanded = projectExpanded, onDismissRequest = { projectExpanded = false }) {
                            projects.forEach { p ->
                                DropdownMenuItem(text = { Text(p.title) }, onClick = { selectedProject = p; projectExpanded = false })
                            }
                        }
                    }

                    OutlinedTextField(
                        value = workDate, onValueChange = { workDate = formatDateInput(it) }, 
                        label = { Text("Data (AAAA-MM-DD)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                    )
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = startTime, onValueChange = { startTime = formatTimeInput(it) }, 
                            label = { Text("Início (HH:mm)") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = endTime, onValueChange = { endTime = formatTimeInput(it) }, 
                            label = { Text("Fim (HH:mm)") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)
                        )
                    }

                    if (errorMessage.isNotBlank()) Text(errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)

                    PrimaryButton(text = "Registrar Horas", icon = Icons.Default.Add, modifier = Modifier.fillMaxWidth(), onClick = {
                        if (selectedProject != null && isValidDateFormat(workDate) && isValidTimeFormat(startTime) && isValidTimeFormat(endTime)) {
                            try {
                                val duration = TimeCalculator.calculateDurationMinutes(startTime, endTime)
                                timeEntryDao.insertTimeEntry(TimeEntry(projectId = selectedProject!!.id!!, workDate = workDate, startTime = startTime, endTime = endTime, durationMinutes = duration, notes = notes))
                                workDate = ""; startTime = ""; endTime = ""; errorMessage = ""; refresh()
                            } catch (e: Exception) { errorMessage = e.message ?: "Erro." }
                        } else { errorMessage = "Preencha os dados corretamente." }
                    })
                }
            }

            Spacer(Modifier.height(24.dp))
            SectionTitle("Histórico")

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                items(entries) { entry ->
                    val project = projects.find { it.id == entry.projectId }
                    val totalValue = TimeCalculator.calculateTotalValue(entry.durationMinutes, project?.hourlyRate ?: 0.0)
                    val decimalHours = TimeCalculator.calculateDecimalHours(entry.durationMinutes)

                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(text = project?.title ?: "...", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                    Text(text = entry.workDate, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { entryToDelete = entry; showDeleteDialog = true }) {
                                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f))
                                }
                            }
                            Divider(Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                InfoBlock("Período", "${entry.startTime} - ${entry.endTime}")
                                InfoBlock("Duração", "%.2fh".format(decimalHours))
                                InfoBlock("Ganho", "R$ %.2f".format(totalValue), isHighlight = true)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoBlock(label: String, value: String, isHighlight: Boolean = false) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value, 
            style = MaterialTheme.typography.bodyMedium, 
            fontWeight = FontWeight.Bold,
            color = if (isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * SummaryScreen: Dashboard refinado com métricas claras e ícones básicos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(clientDao: ClientDao, projectDao: ProjectDao, timeEntryDao: TimeEntryDao, onBack: () -> Unit) {
    var totalClients by remember { mutableStateOf(0) }
    var totalProjects by remember { mutableStateOf(0) }
    var totalHours by remember { mutableStateOf(0.0) }
    var totalValue by remember { mutableStateOf(0.0) }
    var statusCounts by remember { mutableStateOf(mapOf<String, Int>()) }

    LaunchedEffect(Unit) {
        val clients = clientDao.getAllClients()
        val projects = projectDao.getAllProjects()
        val entries = timeEntryDao.getAllTimeEntries()
        totalClients = clients.size
        totalProjects = projects.size
        totalHours = TimeCalculator.calculateDecimalHours(entries.sumOf { it.durationMinutes })
        var sum = 0.0
        entries.forEach { e -> 
            val p = projects.find { it.id == e.projectId }
            if (p != null) sum += TimeCalculator.calculateTotalValue(e.durationMinutes, p.hourlyRate)
        }
        totalValue = sum
        statusCounts = projects.groupBy { it.status.lowercase() }.mapValues { it.value.size }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Resumo Geral", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } })
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Substituído Icons.Default.People por Icons.Default.Person
                MetricCard("Clientes", totalClients.toString(), Icons.Default.Person, Modifier.weight(1f))
                // Substituído Icons.Default.Folder por Icons.Default.List
                MetricCard("Projetos", totalProjects.toString(), Icons.Default.List, Modifier.weight(1f))
            }

            // Substituído Icons.Default.Payments por Icons.Default.CheckCircle
            MetricCardLarge("Ganhos Totais Estimados", "R$ %.2f".format(totalValue), "Total de %.1f horas trabalhadas".format(totalHours), Icons.Default.CheckCircle)

            SectionTitle("Projetos por Status")
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SummaryStatusRow("Ativos", statusCounts["ativo"] ?: 0, Color(0xFF16A34A))
                    SummaryStatusRow("Pausados", statusCounts["pausado"] ?: 0, Color(0xFFF59E0B))
                    SummaryStatusRow("Finalizados", statusCounts["finalizado"] ?: 0, Color(0xFF2563EB))
                }
            }
        }
    }
}

@Composable
fun MetricCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun MetricCardLarge(label: String, value: String, footer: String, icon: ImageVector) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)) {
        Row(Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(text = label, color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelLarge)
                Text(text = value, color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Text(text = footer, color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
            }
            Icon(icon, null, tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(64.dp))
        }
    }
}

@Composable
fun SummaryStatusRow(label: String, count: Int, color: Color) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(12.dp).background(color, RoundedCornerShape(2.dp)))
        Spacer(Modifier.width(12.dp))
        Text(text = label, Modifier.weight(1f))
        Text(text = count.toString(), fontWeight = FontWeight.Bold)
    }
}

/**
 * Placeholder genérico.
 */
@Composable
fun PlaceholderScreen(title: String, modifier: Modifier = Modifier, onBack: () -> Unit) {
    Column(modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Button(onClick = onBack) { Text("Voltar") }
    }
}

// --- FUNÇÕES AUXILIARES ---

private fun formatDateInput(input: String): String {
    val digits = input.filter { it.isDigit() }.take(8)
    return buildString {
        for (i in digits.indices) {
            append(digits[i])
            if ((i == 3 || i == 5) && i < digits.length - 1) append("-")
        }
    }
}

private fun formatTimeInput(input: String): String {
    val digits = input.filter { it.isDigit() }.take(4)
    return buildString {
        for (i in digits.indices) {
            append(digits[i])
            if (i == 1 && i < digits.length - 1) append(":")
        }
    }
}

private fun isValidDateFormat(date: String): Boolean = date.length == 10 && Regex("^\\d{4}-\\d{2}-\\d{2}$").matches(date)
private fun isValidTimeFormat(time: String): Boolean = time.length == 5 && Regex("^\\d{2}:\\d{2}$").matches(time)
