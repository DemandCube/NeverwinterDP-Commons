import requests, click, json

#r = requests.get("http://www.github.com/DemandCube/Scribengin")
#print r.content


@click.group()
@click.option('--debug/--no-debug', default=False)
def mastercommand(debug):
    click.echo('Debug mode is %s' % ('on' if debug else 'off'))

@mastercommand.command()
@click.option('--count', default=1, help='Number of poops.')
def poop(count):
    click.echo('So many poops!')
    for x in range(count):
      click.echo('pooping: '+ `x`)

@mastercommand.command()
@click.option('--user', prompt='github user name')
@click.option('--pw', prompt='github password ', hide_input=True)
@click.option('--repo', prompt='repo to create ', default="test")
def createGitRepo(user, pw, repo):
    click.echo('request')
    github_url = "https://api.github.com/user/repos"
    data = json.dumps({'name':repo, 'description':'Automatically created repo'}) 
    r = requests.post(github_url, data, auth=(user, pw))
    print r.content
    print r.json


if __name__ == '__main__':
  mastercommand()

  