// In-memory data store
let wellnessData = {
  entries: [
    // Sample initial data
    { date: new Date(Date.now() - 6 * 86400000).toISOString(), mood: 'happy', stress: 3, sleep: 7.5, energy: 7, gratitude: 'The sunshine and my morning coffee' },
    { date: new Date(Date.now() - 5 * 86400000).toISOString(), mood: 'okay', stress: 5, sleep: 6, energy: 5, gratitude: 'A productive work meeting' },
    { date: new Date(Date.now() - 4 * 86400000).toISOString(), mood: 'happy', stress: 2, sleep: 8, energy: 8, gratitude: 'Dinner with friends' },
    { date: new Date(Date.now() - 3 * 86400000).toISOString(), mood: 'anxious', stress: 7, sleep: 5.5, energy: 4, gratitude: 'Finishing a difficult task' },
    { date: new Date(Date.now() - 2 * 86400000).toISOString(), mood: 'sad', stress: 6, sleep: 7, energy: 5, gratitude: 'A kind message from a colleague' },
    { date: new Date(Date.now() - 1 * 86400000).toISOString(), mood: 'okay', stress: 4, sleep: 7, energy: 6, gratitude: 'Time to read a book' },
  ]
};

// DOM Elements
const moodIcons = document.querySelectorAll('.mood-icon');
const stressSlider = document.getElementById('stress');
const stressValue = document.getElementById('stressValue');
const energySlider = document.getElementById('energy');
const energyValue = document.getElementById('energyValue');
const trackerForm = document.getElementById('trackerForm');
const themeToggle = document.getElementById('themeToggle');
const analyticsPreview = document.getElementById('analyticsPreview');

// Initialize the app when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
  // Set theme
  const savedTheme = localStorage.getItem('theme');
  const systemPrefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
  
  if (savedTheme === 'dark' || (!savedTheme && systemPrefersDark)) {
    document.documentElement.setAttribute('data-bs-theme', 'dark');
  }
  
  // Initialize form values
  updateStressBadgeColor(stressSlider.value);
  updateEnergyBadgeColor(energySlider.value);
  
  // Initialize visualizations
  updateSummary();
  updateAnalyticsPreview();
});

// Event Listeners
moodIcons.forEach(icon => {
  icon.addEventListener('click', function() {
    moodIcons.forEach(i => i.classList.remove('selected'));
    this.classList.add('selected');
    document.getElementById('mood').value = this.dataset.mood;
  });
});

stressSlider.addEventListener('input', function() {
  stressValue.textContent = this.value;
  updateStressBadgeColor(this.value);
});

energySlider.addEventListener('input', function() {
  energyValue.textContent = this.value;
  updateEnergyBadgeColor(this.value);
});

themeToggle.addEventListener('click', toggleTheme);

trackerForm.addEventListener('submit', function(e) {
  e.preventDefault();
  
  const mood = document.getElementById('mood').value;
  const stress = stressSlider.value;
  const sleep = document.getElementById('sleep').value;
  const energy = energySlider.value;
  const gratitude = document.getElementById('gratitude').value;
  
  if (!mood) {
    showAlert('Please select your mood', 'danger');
    return;
  }
  
  if (!sleep || isNaN(sleep)) {
    showAlert('Please enter a valid sleep duration', 'danger');
    return;
  }
  
  saveEntry(mood, stress, sleep, energy, gratitude);
  updateSummary();
  updateAnalyticsPreview();
  resetForm();
  showAlert('Entry saved successfully!', 'success');
   sessionStorage.setItem('wellnessData', JSON.stringify(wellnessData));
  
  // Redirect to analytics page
  window.location.href = 'analytics.html';
});

// Data Functions
function saveEntry(mood, stress, sleep, energy, gratitude) {
  // Check if entry exists for today
  const today = new Date().toISOString().split('T')[0];
  const existingIndex = wellnessData.entries.findIndex(entry => 
    new Date(entry.date).toISOString().split('T')[0] === today
  );
  
  const newEntry = {
    date: new Date().toISOString(),
    mood,
    stress: parseInt(stress),
    sleep: parseFloat(sleep),
    energy: parseInt(energy),
    gratitude
  };
  
  if (existingIndex >= 0) {
    wellnessData.entries[existingIndex] = newEntry;
  } else {
    wellnessData.entries.push(newEntry);
  }
}

// UI Update Functions
function updateSummary() {
  const summaryContainer = document.getElementById('summary');
  
  if (wellnessData.entries.length === 0) {
    summaryContainer.innerHTML = `
      <div class="col-12">
        <div class="alert alert-info">No entries yet. Complete your first check-in to see your stats.</div>
      </div>
    `;
    return;
  }
  
  // Calculate averages for last 7 days
  const lastWeekEntries = wellnessData.entries.slice(-7);
  
  const totalStress = lastWeekEntries.reduce((sum, entry) => sum + entry.stress, 0);
  const totalSleep = lastWeekEntries.reduce((sum, entry) => sum + entry.sleep, 0);
  const totalEnergy = lastWeekEntries.reduce((sum, entry) => sum + entry.energy, 0);
  
  const happyDays = lastWeekEntries.filter(entry => entry.mood === 'happy').length;
  
  const avgStress = (totalStress / lastWeekEntries.length).toFixed(1);
  const avgSleep = (totalSleep / lastWeekEntries.length).toFixed(1);
  const avgEnergy = (totalEnergy / lastWeekEntries.length).toFixed(1);
  const happyPercentage = Math.round((happyDays / lastWeekEntries.length) * 100);
  
  // Determine colors based on values
  const stressColor = avgStress > 6 ? 'danger' : avgStress > 3 ? 'warning' : 'success';
  const sleepColor = avgSleep < 6 ? 'danger' : avgSleep < 7 ? 'warning' : 'success';
  const energyColor = avgEnergy < 4 ? 'danger' : avgEnergy < 6 ? 'warning' : 'success';
  
  summaryContainer.innerHTML = `
    <div class="col-md-6 col-lg-3 mb-4">
      <div class="card card-stat h-100">
        <div class="card-body text-center">
          <div class="stat-icon mb-3 text-${stressColor}">😰</div>
          <h3 class="card-title">${avgStress}/10</h3>
          <p class="card-text">Average Stress</p>
          <div class="progress">
            <div class="progress-bar bg-${stressColor}" style="width: ${avgStress * 10}%"></div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="col-md-6 col-lg-3 mb-4">
      <div class="card card-stat h-100">
        <div class="card-body text-center">
          <div class="stat-icon mb-3 text-${sleepColor}">😴</div>
          <h3 class="card-title">${avgSleep}h</h3>
          <p class="card-text">Average Sleep</p>
          <div class="progress">
            <div class="progress-bar bg-${sleepColor}" style="width: ${(avgSleep / 9) * 100}%"></div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="col-md-6 col-lg-3 mb-4">
      <div class="card card-stat h-100">
        <div class="card-body text-center">
          <div class="stat-icon mb-3 text-${energyColor}">⚡</div>
          <h3 class="card-title">${avgEnergy}/10</h3>
          <p class="card-text">Average Energy</p>
          <div class="progress">
            <div class="progress-bar bg-${energyColor}" style="width: ${avgEnergy * 10}%"></div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="col-md-6 col-lg-3 mb-4">
      <div class="card card-stat h-100">
        <div class="card-body text-center">
          <div class="stat-icon mb-3 text-primary">😊</div>
          <h3 class="card-title">${happyPercentage}%</h3>
          <p class="card-text">Happy Days</p>
          <div class="progress">
            <div class="progress-bar bg-success" style="width: ${happyPercentage}%"></div>
          </div>
        </div>
      </div>
    </div>
  `;
}

function updateAnalyticsPreview() {
  if (!analyticsPreview) return;
  
  if (wellnessData.entries.length < 2) {
    analyticsPreview.innerHTML = `
      <div class="col-12">
        <div class="alert alert-info">Complete a few more check-ins to see analytics preview.</div>
      </div>
    `;
    return;
  }
  
  // Create mini mood chart
  const moodCounts = getMoodCounts();
  analyticsPreview.innerHTML = `
    <div class="col-md-6 mb-4">
      <div class="card h-100">
        <div class="card-body">
          <h5 class="card-title">Mood Distribution</h5>
          <div style="height: 200px;">
            <canvas id="miniMoodChart"></canvas>
          </div>
        </div>
      </div>
    </div>
    <div class="col-md-6 mb-4">
      <div class="card h-100">
        <div class="card-body">
          <h5 class="card-title">Stress vs Sleep</h5>
          <div style="height: 200px;">
            <canvas id="miniStressSleepChart"></canvas>
          </div>
        </div>
      </div>
    </div>
  `;
  
  // Render mini mood chart
  const moodCtx = document.getElementById('miniMoodChart').getContext('2d');
  new Chart(moodCtx, {
    type: 'doughnut',
    data: {
      labels: ['Happy', 'Okay', 'Sad', 'Anxious', 'Angry'],
      datasets: [{
        data: [
          moodCounts.happy || 0,
          moodCounts.okay || 0,
          moodCounts.sad || 0,
          moodCounts.anxious || 0,
          moodCounts.angry || 0
        ],
        backgroundColor: [
          '#2dce89',
          '#11cdef',
          '#f5365c',
          '#fb6340',
          '#5e72e4'
        ]
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'bottom',
        }
      },
      cutout: '60%'
    }
  });
  
  // Render mini stress vs sleep chart
  const weekData = getWeekData();
  const stressSleepData = weekData
    .filter(entry => entry !== null && !isNaN(entry.sleep) && !isNaN(entry.stress))
    .map(entry => ({
      x: entry.sleep,
      y: entry.stress
    }));
  
  const stressSleepCtx = document.getElementById('miniStressSleepChart').getContext('2d');
  new Chart(stressSleepCtx, {
    type: 'scatter',
    data: {
      datasets: [{
        label: 'Stress vs Sleep',
        data: stressSleepData,
        backgroundColor: '#5e72e4',
        pointRadius: 5
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        x: {
          title: { display: true, text: 'Sleep Hours' },
          min: 0,
          max: 12
        },
        y: {
          title: { display: true, text: 'Stress Level' },
          min: 1,
          max: 10
        }
      }
    }
  });
}
// Add these functions to your existing script.js

function renderMiniMoodChart() {
  const moodCounts = getMoodCounts(wellnessData);
  const ctx = document.getElementById('miniMoodChart').getContext('2d');
  
  new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: ['Happy', 'Okay', 'Sad', 'Anxious', 'Angry'],
      datasets: [{
        data: [
          moodCounts.happy || 0,
          moodCounts.okay || 0,
          moodCounts.sad || 0,
          moodCounts.anxious || 0,
          moodCounts.angry || 0
        ],
        backgroundColor: [
          '#2dce89',
          '#11cdef',
          '#f5365c',
          '#fb6340',
          '#5e72e4'
        ],
        borderWidth: 0
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'bottom',
        },
        tooltip: {
          callbacks: {
            label: function(context) {
              const label = context.label || '';
              const value = context.raw || 0;
              const total = context.dataset.data.reduce((a, b) => a + b, 0);
              const percentage = total > 0 ? Math.round((value / total) * 100) : 0;
              return `${label}: ${value} (${percentage}%)`;
            }
          }
        }
      },
      cutout: '70%'
    }
  });
}

function renderMiniStressSleepChart() {
  const weekData = getWeekData(wellnessData);
  const stressSleepData = weekData
    .filter(entry => entry !== null && !isNaN(entry.sleep) && !isNaN(entry.stress))
    .map(entry => ({
      x: entry.sleep,
      y: entry.stress,
      date: entry.date
    }));
  
  const ctx = document.getElementById('miniStressSleepChart').getContext('2d');
  
  new Chart(ctx, {
    type: 'scatter',
    data: {
      datasets: [{
        label: 'Stress vs Sleep',
        data: stressSleepData,
        backgroundColor: '#5e72e4',
        pointRadius: 6,
        pointHoverRadius: 8
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        x: {
          title: {
            display: true,
            text: 'Sleep Hours'
          },
          min: 0,
          max: 12
        },
        y: {
          title: {
            display: true,
            text: 'Stress Level'
          },
          min: 1,
          max: 10
        }
      },
      plugins: {
        tooltip: {
          callbacks: {
            label: function(context) {
              const date = new Date(context.raw.date);
              const formattedDate = date.toLocaleDateString();
              return [
                `Date: ${formattedDate}`,
                `Stress: ${context.parsed.y}`,
                `Sleep: ${context.parsed.x} hours`
              ];
            }
          }
        }
      }
    }
  });
}

// Update your DOMContentLoaded or initialization function
document.addEventListener('DOMContentLoaded', function() {
  // ... existing initialization code ...
  
  // Render mini charts
  if (wellnessData.entries.length > 0) {
    renderMiniMoodChart();
    renderMiniStressSleepChart();
  }
  
  // Update form submission to refresh charts
  trackerForm.addEventListener('submit', function(e) {
    e.preventDefault();
    
    // ... existing form handling code ...
    
    // After saving data
    renderMiniMoodChart();
    renderMiniStressSleepChart();
    updateSummary();
    
    showAlert('Entry saved successfully!', 'success');
  });
});

// Helper Functions
function getWeekData() {
  const lastSevenDays = getLastSevenDays();
  return lastSevenDays.map(day => {
    const entry = wellnessData.entries.find(e => 
      new Date(e.date).toISOString().split('T')[0] === day
    );
    return entry || null;
  });
}

function getLastSevenDays() {
  const dates = [];
  for (let i = 6; i >= 0; i--) {
    const date = new Date();
    date.setDate(date.getDate() - i);
    dates.push(date.toISOString().split('T')[0]);
  }
  return dates;
}

function getMoodCounts() {
  const counts = {
    happy: 0,
    okay: 0,
    sad: 0,
    anxious: 0,
    angry: 0
  };
  
  wellnessData.entries.forEach(entry => {
    if (entry && counts.hasOwnProperty(entry.mood)) {
      counts[entry.mood]++;
    }
  });
  
  return counts;
}

function showAlert(message, type) {
  const alert = document.createElement('div');
  alert.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
  alert.style.bottom = '20px';
  alert.style.right = '20px';
  alert.style.zIndex = '1100';
  alert.style.maxWidth = '300px';
  alert.role = 'alert';
  alert.innerHTML = `
    ${message}
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
  `;
  
  document.body.appendChild(alert);
  
  setTimeout(() => {
    alert.classList.remove('show');
    setTimeout(() => alert.remove(), 150);
  }, 3000);
}

function updateStressBadgeColor(value) {
  if (value <= 3) {
    stressValue.className = 'badge bg-success';
  } else if (value <= 6) {
    stressValue.className = 'badge bg-warning text-dark';
  } else {
    stressValue.className = 'badge bg-danger';
  }
}

function updateEnergyBadgeColor(value) {
  if (value <= 3) {
    energyValue.className = 'badge bg-danger';
  } else if (value <= 6) {
    energyValue.className = 'badge bg-warning text-dark';
  } else {
    energyValue.className = 'badge bg-success';
  }
}

function toggleTheme() {
  const html = document.documentElement;
  const currentTheme = html.getAttribute('data-bs-theme');
  const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
  html.setAttribute('data-bs-theme', newTheme);
  localStorage.setItem('theme', newTheme);
}

function resetForm() {
  moodIcons.forEach(i => i.classList.remove('selected'));
  document.getElementById('mood').value = '';
  stressSlider.value = 5;
  stressValue.textContent = '5';
  updateStressBadgeColor(5);
  document.getElementById('sleep').value = '';
  energySlider.value = 5;
  energyValue.textContent = '5';
  updateEnergyBadgeColor(5);
  document.getElementById('gratitude').value = '';
}