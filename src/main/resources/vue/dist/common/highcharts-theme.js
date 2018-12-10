/**
 * Dark blue theme for Highcharts JS
 * @author Torstein Hønsi
 */

Highcharts.theme = {
	colors: ['#7cb5ec', '#434348', '#90ed7d', '#f7a35c', '#8bbc21', '#910000', '#1aadce', '#492970', '#f28f43', '#77a1e5', '#c42525', '#a6c96a'],
	chart: {
		backgroundColor: 'rgb(238,238,238)',
		borderColor: 'rgba(0,0,0,0)',
		borderWidth: 0,
		className: 'dark-container',
		plotBackgroundColor: 'rgba(255, 255, 255, 0)',
		plotBorderColor: 'rgba(0,0,0,0)',
		plotBorderWidth: 0
	},

	title: {
		style: {
			color: '#000000',
			font: '16px "Trebuchet MS", Verdana, sans-serif'
		}
	},
	subtitle: {
		style: {
			color: '#393939',
			font: '12px "Trebuchet MS", Verdana, sans-serif'
		}
	},
	xAxis: {
		gridLineColor: 'rgba(0,0,0,0.1)',
		gridLineWidth: 1,
		labels: {
			style: {
				color: 'rgb(100,100,100)'
			}
		},
		lineColor: 'rgb(100,100,100)',
		tickColor: '#646464',
		title: {
			style: {
				color: 'rgb(80,80,80)',
				fontWeight: 'lighter',
				fontSize: '12px',
				fontFamily: 'Trebuchet MS, Verdana, sans-serif'

			}
		}
	},
	yAxis: {
		gridLineColor: 'rgba(0,0,0,0.1)',
		labels: {
			style: {
				color: 'rgb(100,100,100)'
			}
		},
		lineColor: 'rgb(100,100,100)',
		lineWidth: 1,
		minorTickInterval: null,
		tickColor: '#646464',
		tickWidth: 1,
		title: {
			style: {
				color: 'rgb(80,80,80)',
				fontWeight: 'lighter',
				fontSize: '12px',
				fontFamily: 'Trebuchet MS, Verdana, sans-serif'
			}
		}
	},
	tooltip: {
		backgroundColor: 'rgba(255,255,255, 0.5)',
		style: {
			color: 'rgb(50,50,50)'
		}
	},
	toolbar: {
		itemStyle: {
			color: 'silver'
		}
	},
	
	plotOptions: {
		series: {
			borderColor: 'rgba(0,0,0,0)'
			},
		line: {
			dataLabels: {
				color: '#CCC'
			},
			marker: {
				lineColor: '#333'
			}
		},
		spline: {
			marker: {
				lineColor: '#333'
			}
		},
		scatter: {
			marker: {
				lineColor: '#333'
			}
		},
		candlestick: {
			lineColor: 'white'
		}
	},
	legend: {
		itemStyle: {
			font: '9pt Trebuchet MS, Verdana, sans-serif',
			color: 'rgb(100,100,100)'
		},
		itemHoverStyle: {
			color: '#FFF'
		},
		itemHiddenStyle: {
			color: '#CCC'
		},
	},
	credits: {
		style: {
			color: '#666'
		}
	},
	labels: {
		style: {
			color: '#CCC',

		}
	},

	navigation: {
		buttonOptions: {
			symbolStroke: '#DDDDDD',
			hoverSymbolStroke: '#FFFFFF',
			theme: {
				fill: {
					linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
					stops: [
						[0.4, '#606060'],
						[0.6, '#333333']
					]
				},
				stroke: '#000000'
			}
		}
	},

	// scroll charts
	rangeSelector: {
		buttonTheme: {
			fill: 'rgba(255,255,255,0.3)',
			stroke: '#000000',
			style: {
				color: '#CCC',
				fontWeight: 'bold'
			},
			states: {
				hover: {
					fill: {
						linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
						stops: [
							[0.4, '#BBB'],
							[0.6, '#888']
						]
					},
					stroke: '#000000',
					style: {
						color: 'white'
					}
				},
				select: {
					fill: {
						linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
						stops: [
							[0.1, '#000'],
							[0.3, '#333']
						]
					},
					stroke: '#000000',
					style: {
						color: 'yellow'
					}
				}
			}
		},
		inputStyle: {
			backgroundColor: '#333',
			color: 'silver'
		},
		labelStyle: {
			color: 'silver'
		}
	},

	navigator: {
		handles: {
			backgroundColor: '#666',
			borderColor: '#AAA'
		},
		outlineColor: '#CCC',
		maskFill: 'rgba(16, 16, 16, 0.5)',
		series: {
			color: '#7798BF',
			lineColor: '#A6C7ED'
		}
	},

	scrollbar: {
		barBackgroundColor: {
				linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
				stops: [
					[0.4, '#888'],
					[0.6, '#555']
				]
			},
		barBorderColor: '#CCC',
		buttonArrowColor: '#CCC',
		buttonBackgroundColor: {
				linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
				stops: [
					[0.4, '#888'],
					[0.6, '#555']
				]
			},
		buttonBorderColor: '#CCC',
		rifleColor: '#FFF',
		trackBackgroundColor: 'rgba(120,0,0,0)',
		trackBorderColor: '#666'
	},
	credits: {
		enabled: false
	},

	// special colors for some of the
	legendBackgroundColor: 'rgba(50,50,50,0)',
	legendBackgroundColorSolid: 'rgb(35, 35, 70)',
	dataLabelsColor: '#444',
	textColor: '#C0C0C0',
	maskColor: 'rgba(255,255,255,0)'
};

// Apply the theme
Highcharts.setOptions(Highcharts.theme);
