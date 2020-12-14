import seaborn as sns
import pandas as pd
import matplotlib.pyplot as plt

dataset = pd.read_csv('robocode_4robots.csv')
sns.pairplot(dataset,diag_kind = 'kde',plot_kws = {'alpha': 0.6, 's': 80, 'edgecolor': 'k'})
plt.suptitle('Norov top graf', size=30)
plt.show()
